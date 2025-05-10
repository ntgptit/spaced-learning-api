package com.spacedlearning.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting user with ID: {}", id);

        final var user = this.userRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));

        user.softDelete();
        this.userRepository.save(user);

        log.info("User soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailedResponse> findAll(Pageable pageable) {
        log.debug("Retrieving all users with pagination: {}", pageable);
        return this.userRepository.findAll(pageable)
                .map(this.userMapper::toDetailedDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailedResponse findById(UUID id) {
        log.debug("Finding user by ID: {}", id);

        final var user = this.userRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));

        return this.userMapper.toDetailedDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByUsernameOrEmail(String input) {
        log.debug("Finding user by username or email: {}", input);

        final var user = this.userRepository.findByUsernameOrEmail(input)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("User with username/email: " + input,
                        null));

        return this.userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        final var authentication = Optional.ofNullable(SecurityContextHolder.getContext()
                .getAuthentication())
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> SpacedLearningException.forbidden("User not authenticated"));

        final var principal = authentication.getName();
        log.debug("Retrieving current user by principal: {}", principal);

        return findByUsernameOrEmail(principal);
    }

    @Override
    @Transactional
    public UserResponse restore(UUID id) {
        log.debug("Restoring user with ID: {}", id);

        final var user = this.userRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));

        if (!user.isDeleted()) {
            log.info("User with ID: {} is not marked as deleted. Skip restore.", id);
            return this.userMapper.toDto(user);
        }

        user.restore();
        final var restoredUser = this.userRepository.save(user);

        log.info("User restored successfully with ID: {}", restoredUser.getId());
        return this.userMapper.toDto(restoredUser);
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        log.debug("Updating user with ID: {}, request: {}", id, request);

        final var user = this.userRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));

        this.userMapper.updateFromDto(request, user);
        final var updatedUser = this.userRepository.save(user);

        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return this.userMapper.toDto(updatedUser);
    }
}
