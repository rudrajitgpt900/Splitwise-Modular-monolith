package com.example.splitwise.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @Email(message = "invalid email") @NotBlank(message = "email is required") String email,
        @NotBlank(message = "displayName is required") @Size(max = 120, message = "displayName too long") String displayName
) {}

