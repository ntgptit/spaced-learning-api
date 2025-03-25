// File: src/main/java/com/spacedlearning/service/impl/UserServiceImpl.java
package com.spacedlearning.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.security.CustomUserDetailsService;
import com.spacedlearning.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final CustomUserDetailsService userDetailsService;

	@Override
	@Transactional
	public void delete(UUID id) {
		log.debug("Deleting user with ID: {}", id);

		final User user = userRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));

		user.softDelete(); // Use soft delete
		userRepository.save(user);

		log.info("User soft deleted successfully with ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserDetailedResponse> findAll(Pageable pageable) {
		log.debug("Finding all users with pagination: {}", pageable);
		return userRepository.findAll(pageable).map(userMapper::toDetailedDto);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse findByEmail(String email) {
		log.debug("Finding user by email: {}", email);
		final User user = userRepository.findByEmail(email)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("User with email " + email, null));
		return userMapper.toDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailedResponse findById(UUID id) {
		log.debug("Finding user by ID: {}", id);
		final User user = userRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));
		return userMapper.toDetailedDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getCurrentUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw SpacedLearningException.forbidden("User not authenticated");
		}

		final String email = authentication.getName();
		log.debug("Getting current user with email: {}", email);

		return findByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Loading user details by username: {}", username);
		return userDetailsService.loadUserByUsername(username);
	}

	@Override
    @Transactional
    public UserResponse restore(UUID id) {
        log.debug("Restoring user with ID: {}", id);

        final User user = userRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));

        if (!user.isDeleted()) {
            log.info("User with ID: {} is not deleted, no restoration needed", id);
            return userMapper.toDto(user);
        }

        user.restore();
        final User restoredUser = userRepository.save(user);

        log.info("User restored successfully with ID: {}", restoredUser.getId());
        return userMapper.toDto(restoredUser);
    }

	@Override
	@Transactional
	public UserResponse update(UUID id, UserUpdateRequest request) {
		log.debug("Updating user with ID: {}, request: {}", id, request);

		final User user = userRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("User", id));

		userMapper.updateFromDto(request, user);
		final User updatedUser = userRepository.save(user);

		log.info("User updated successfully with ID: {}", updatedUser.getId());
		return userMapper.toDto(updatedUser);
	}
}