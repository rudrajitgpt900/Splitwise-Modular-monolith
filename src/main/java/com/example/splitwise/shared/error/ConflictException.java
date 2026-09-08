package com.example.splitwise.shared.error;

/**
 * Thrown when a request conflicts with the current state of the server, e.g. unique constraint violation.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

