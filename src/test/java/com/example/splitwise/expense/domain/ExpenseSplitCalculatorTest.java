package com.example.splitwise.expense.domain;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

/**
 * Protects the money-splitting rules before they are used by a controller or database adapter.
 */
class ExpenseSplitCalculatorTest {

    private static final UUID ALICE = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID BOB = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID CAROL = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Test
    void equalSplitAssignsRemainderDeterministically() {
        List<ExpenseSplit> result = ExpenseSplitCalculator.calculate(
                100,
                SplitMethod.EQUAL,
                List.of(CAROL, ALICE, BOB),
                null,
                null);

        assertThat(result).extracting(split -> split.userId()).containsExactly(ALICE, BOB, CAROL);
        assertThat(result).extracting(split -> split.shareMinor()).containsExactly(34L, 33L, 33L);
    }

    @Test
    void exactSplitRequiresTheRecordedAmount() {
        List<ExpenseSplit> result = ExpenseSplitCalculator.calculate(
                100,
                SplitMethod.EXACT,
                List.of(ALICE, BOB),
                Map.of(ALICE, 60L, BOB, 40L),
                null);

        assertThat(result).extracting(split -> split.shareMinor()).containsExactly(60L, 40L);
    }

    @Test
    void percentageSplitPreservesTotalAfterRounding() {
        List<ExpenseSplit> result = ExpenseSplitCalculator.calculate(
                101,
                SplitMethod.PERCENTAGE,
                List.of(ALICE, BOB, CAROL),
                null,
                Map.of(ALICE, 3333, BOB, 3333, CAROL, 3334));

        assertThat(result).extracting(split -> split.shareMinor()).containsExactly(34L, 33L, 34L);
        assertThat(result.stream().mapToLong(split -> split.shareMinor()).sum()).isEqualTo(101L);
    }

    @Test
    void invalidExactTotalIsRejected() {
        assertThatThrownBy(() -> ExpenseSplitCalculator.calculate(
                100,
                SplitMethod.EXACT,
                List.of(ALICE, BOB),
                Map.of(ALICE, 60L, BOB, 41L),
                null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expense shares must total the expense amount");
    }
}
