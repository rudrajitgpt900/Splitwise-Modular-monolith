package com.example.splitwise.expense.domain;

import java.util.UUID;

/**
 * Holds the amount assigned to one participant after a split is calculated.
 * The amount is stored in minor currency units to keep all arithmetic exact.
 */
public record ExpenseSplit(UUID userId, long shareMinor, Integer percentageBasisPoints) {

    public ExpenseSplit {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (shareMinor < 0) {
            throw new IllegalArgumentException("shareMinor must not be negative");
        }
        if (percentageBasisPoints != null && (percentageBasisPoints < 0 || percentageBasisPoints > 10_000)) {
            throw new IllegalArgumentException("percentageBasisPoints must be between 0 and 10000");
        }
    }
}
