package com.example.splitwise.user.api.dto;

import java.time.Instant;

public record UserResponse(
        String id,
        String email,
        String displayName,
        Instant createdAt,
        Instant updatedAt
) {}

