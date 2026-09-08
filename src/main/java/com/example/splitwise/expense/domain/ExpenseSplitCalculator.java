package com.example.splitwise.expense.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Calculates participant shares while enforcing the accounting invariant that all shares
 * add up exactly to the recorded expense amount.
 */
public final class ExpenseSplitCalculator {

    private static final int BASIS_POINTS_TOTAL = 10_000;

    private ExpenseSplitCalculator() {
        // Utility class: callers use the static calculation method without creating state.
    }

    /**
     * Calculates shares for the requested split method. Participant IDs are sorted before
     * equal-split remainder allocation so retries produce the same result every time.
     */
    public static List<ExpenseSplit> calculate(
            long amountMinor,
            SplitMethod method,
            List<UUID> participantIds,
            Map<UUID, Long> exactShares,
            Map<UUID, Integer> percentages
    ) {
        validateCommonInputs(amountMinor, method, participantIds);
        List<UUID> sortedParticipants = participantIds.stream().sorted().toList();

        return switch (method) {
            case EQUAL -> calculateEqual(amountMinor, sortedParticipants);
            case EXACT -> calculateExact(amountMinor, sortedParticipants, exactShares);
            case PERCENTAGE -> calculatePercentage(amountMinor, sortedParticipants, percentages);
        };
    }

    private static List<ExpenseSplit> calculateEqual(long amountMinor, List<UUID> participants) {
        long baseShare = amountMinor / participants.size();
        long remainder = amountMinor % participants.size();
        List<ExpenseSplit> result = new ArrayList<>();

        for (int index = 0; index < participants.size(); index++) {
            long share = baseShare + (index < remainder ? 1 : 0);
            result.add(new ExpenseSplit(participants.get(index), share, null));
        }
        return result;
    }

    private static List<ExpenseSplit> calculateExact(
            long amountMinor,
            List<UUID> participants,
            Map<UUID, Long> exactShares
    ) {
        if (exactShares == null || exactShares.size() != participants.size()
                || !exactShares.keySet().equals(SetSupport.asSet(participants))) {
            throw new IllegalArgumentException("exact shares must be supplied for every participant");
        }

        List<ExpenseSplit> result = participants.stream()
                .map(userId -> new ExpenseSplit(userId, exactShares.get(userId), null))
                .toList();
        validateTotal(amountMinor, result);
        return result;
    }

    private static List<ExpenseSplit> calculatePercentage(
            long amountMinor,
            List<UUID> participants,
            Map<UUID, Integer> percentages
    ) {
        if (percentages == null || percentages.size() != participants.size()
                || !percentages.keySet().equals(SetSupport.asSet(participants))) {
            throw new IllegalArgumentException("percentages must be supplied for every participant");
        }

        int percentageTotal = percentages.values().stream().mapToInt(value -> value).sum();
        if (percentageTotal != BASIS_POINTS_TOTAL) {
            throw new IllegalArgumentException("percentages must total 10000 basis points");
        }

        Map<UUID, Long> fractionalRemainders = new HashMap<>();
        List<ExpenseSplit> result = new ArrayList<>();
        for (UUID userId : participants) {
            long rawShare = amountMinor * percentages.get(userId);
            result.add(new ExpenseSplit(userId, rawShare / BASIS_POINTS_TOTAL, percentages.get(userId)));
            fractionalRemainders.put(userId, rawShare % BASIS_POINTS_TOTAL);
        }

        long allocated = result.stream().mapToLong(split -> split.shareMinor()).sum();
        long remainder = amountMinor - allocated;
        List<Integer> remainderOrder = IntStream.range(0, participants.size())
            .boxed()
            .sorted(Comparator
                .comparingLong((Integer index) -> fractionalRemainders.get(participants.get(index)))
                .reversed()
                .thenComparingInt(index -> index))
            .toList();
        for (int index = 0; index < remainder; index++) {
            int resultIndex = remainderOrder.get(index);
            ExpenseSplit split = result.get(resultIndex);
            result.set(resultIndex, new ExpenseSplit(
                split.userId(),
                split.shareMinor() + 1,
                split.percentageBasisPoints()));
        }
        validateTotal(amountMinor, result);
        return result;
    }

    private static void validateCommonInputs(long amountMinor, SplitMethod method, List<UUID> participantIds) {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("amountMinor must be positive");
        }
        if (method == null) {
            throw new IllegalArgumentException("split method is required");
        }
        if (participantIds == null || participantIds.isEmpty()) {
            throw new IllegalArgumentException("at least one participant is required");
        }
        if (participantIds.stream().anyMatch(id -> id == null)) {
            throw new IllegalArgumentException("participant IDs must not be null");
        }
        if (participantIds.stream().distinct().count() != participantIds.size()) {
            throw new IllegalArgumentException("participant IDs must be unique");
        }
    }

    private static void validateTotal(long amountMinor, List<ExpenseSplit> splits) {
        if (splits.stream().anyMatch(split -> split.shareMinor() < 0)
            || splits.stream().mapToLong(split -> split.shareMinor()).sum() != amountMinor) {
            throw new IllegalArgumentException("expense shares must total the expense amount");
        }
    }

    private static final class SetSupport {
        private SetSupport() {
            // Nested helper avoids exposing collection implementation details in the domain API.
        }

        private static java.util.Set<UUID> asSet(List<UUID> values) {
            return values.stream().collect(Collectors.toSet());
        }
    }
}
