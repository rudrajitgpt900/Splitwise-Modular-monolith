package com.example.splitwise.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provides access to the authenticated user identity for downstream modules.
 * Never derive identity from request bodies.
 */
@Component
public class CurrentUser {

    public Optional<String> userId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return Optional.ofNullable(user.getUsername());
        }
        // For JWT, extract "sub" claim via the Authentication name
        return Optional.ofNullable(auth.getName());
    }
}

