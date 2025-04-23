// File: src/main/java/com/spacedlearning/service/UserService.java
package com.spacedlearning.service;

import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for User operations
 */
public interface UserService {

    /**
     * Delete a user
     *
     * @param id User ID
     */
    void delete(UUID id);

    /**
     * Check if a user exists by email
     *
     * @param email User email
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all users with pagination
     *
     * @param pageable Pagination information
     * @return Page of user detailed responses
     */
    Page<UserDetailedResponse> findAll(Pageable pageable);

    /**
     * Find user by email
     *
     * @param email User email
     * @return User response
     */
    UserResponse findByUsernameOrEmail(String email);

    /**
     * Find user by ID
     *
     * @param id User ID
     * @return User detailed response
     */
    UserDetailedResponse findById(UUID id);

    /**
     * Get current authenticated user
     *
     * @return User response
     */
    UserResponse getCurrentUser();

    /**
     * Restore a soft-deleted user
     *
     * @param id User ID
     * @return Restored user response
     */
    UserResponse restore(UUID id);

    /**
     * Update a user
     *
     * @param id      User ID
     * @param request User update request
     * @return Updated user response
     */
    UserResponse update(UUID id, UserUpdateRequest request);
}
