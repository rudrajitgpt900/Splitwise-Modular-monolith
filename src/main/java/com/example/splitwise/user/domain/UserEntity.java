package com.example.splitwise.user.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistence entity for application users.
 */
@Entity
@Table(name = "app_user")
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id; // store UUID as string to match CHAR(36)

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UserEntity() {
    }

    public UserEntity(UUID id, String email, String displayName) {
        this.id = id.toString();
        this.email = email;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id != null ? id.toString() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

