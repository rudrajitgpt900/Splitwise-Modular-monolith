package com.example.splitwise.shared.api;

import java.util.List;

/**
 * Stable pagination wrapper to avoid leaking framework-specific page shapes.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {}

