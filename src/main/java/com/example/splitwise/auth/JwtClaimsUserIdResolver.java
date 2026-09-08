package com.example.splitwise.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resolves the application userId from the Authentication.
 * For JWT, Authentication#getName typically maps to the token subject (sub).
 */
@Component
public class JwtClaimsUserIdResolver {

    public Optional<String> resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName());
    }
}
