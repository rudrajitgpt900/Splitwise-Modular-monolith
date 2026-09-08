package com.example.splitwise.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "displayName is required") @Size(max = 120, message = "displayName too long") String displayName
) {}

