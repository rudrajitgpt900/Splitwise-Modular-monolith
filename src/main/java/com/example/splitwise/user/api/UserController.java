package com.example.splitwise.user.api;

import com.example.splitwise.shared.api.PageResponse;
import com.example.splitwise.user.api.dto.CreateUserRequest;
import com.example.splitwise.user.api.dto.UpdateUserRequest;
import com.example.splitwise.user.api.dto.UserResponse;
import com.example.splitwise.user.domain.UserEntity;
import com.example.splitwise.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserEntity created = service.create(request.email(), request.displayName());
        return ResponseEntity.created(URI.create("/api/v1/users/" + created.getId())).body(toResponse(created));
    }

    @Operation(summary = "Get a user by id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable String id) {
        UserEntity user = service.getById(id);
        return ResponseEntity.ok(toResponse(user));
    }

    @Operation(summary = "Update a user display name")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        UserEntity updated = service.updateDisplayName(id, request.displayName());
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "List users with pagination")
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int size,
            @RequestParam(defaultValue = "createdAt,DESC") String sort
    ) {
        Sort sortSpec = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);
        Page<UserEntity> result = service.list(pageable);
        List<UserResponse> content = result.getContent().stream().map(UserController::toResponse).toList();
        PageResponse<UserResponse> body = new PageResponse<>(content,
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(),
                result.isFirst(), result.isLast());
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Find a user by email")
    @GetMapping("/by-email")
    public ResponseEntity<UserResponse> getByEmail(@RequestParam @Email String email) {
        UserEntity user = service.getByEmail(email);
        return ResponseEntity.ok(toResponse(user));
    }

    private static Sort parseSort(String sort) {
        // Expect format: field,DIR ; currently only allow specific safe fields
        String[] parts = sort.split(",");
        String field = parts.length > 0 ? parts[0].trim() : "createdAt";
        String dir = parts.length > 1 ? parts[1].trim().toUpperCase() : "DESC";
        // Whitelist sortable fields to avoid exposing internals
        if (!field.equals("createdAt") && !field.equals("updatedAt") && !field.equals("email")) {
            field = "createdAt";
        }
        Sort.Direction direction = dir.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private static UserResponse toResponse(UserEntity entity) {
        return new UserResponse(entity.getId(), entity.getEmail(), entity.getDisplayName(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
