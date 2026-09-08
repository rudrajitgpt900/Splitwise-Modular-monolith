package com.example.splitwise.shared.error;

import java.time.Instant;

/**
 * Stable error payload shape for clients and operators. The code is suitable for programmatic handling,
 * while the message remains safe and readable for a human user.
 */
public record ApiError(String code, String message, Instant timestamp) {
}
