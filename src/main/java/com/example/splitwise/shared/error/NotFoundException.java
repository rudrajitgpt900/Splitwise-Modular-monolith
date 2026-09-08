package com.example.splitwise.shared.error;

/**
 * Thrown when a requested resource does not exist.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

