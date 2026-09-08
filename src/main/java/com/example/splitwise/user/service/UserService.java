package com.example.splitwise.user.service;

import com.example.splitwise.shared.error.ConflictException;
import com.example.splitwise.shared.error.NotFoundException;
import com.example.splitwise.user.domain.UserEntity;
import com.example.splitwise.user.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserEntity create(String email, String displayName) {
        repository.findByEmail(email).ifPresent(u -> {
            throw new ConflictException("email already in use");
        });
        UserEntity entity = new UserEntity(UUID.randomUUID(), email.trim().toLowerCase(), displayName.trim());
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public UserEntity getById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public UserEntity getByEmail(String email) {
        return repository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    @Transactional
    public UserEntity updateDisplayName(String id, String displayName) {
        UserEntity entity = getById(id);
        entity.setDisplayName(displayName.trim());
        return repository.save(entity);
    }
}
