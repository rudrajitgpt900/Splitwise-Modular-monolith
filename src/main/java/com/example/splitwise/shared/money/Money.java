package com.example.splitwise.shared.money;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Represents a positive monetary amount using the smallest currency unit, such as cents.
 * Integer storage prevents floating-point rounding from changing a user's balance.
 */
public record Money(
        @Positive(message = "amountMinor must be positive") long amountMinor,
        @NotBlank(message = "currency is required") String currency
) {

    public Money {
        Objects.requireNonNull(currency, "currency must not be null");
        currency = currency.toUpperCase();
        if (currency.length() != 3) {
            throw new IllegalArgumentException("currency must be a three-letter ISO 4217 code");
        }
    }
}
