// File: src/main/java/com/spacedlearning/service/UserService.java
package com.spacedlearning.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;

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
	UserResponse findByEmail(String email);

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
	 * Get UserDetails for a user by username (email)
	 * 
	 * @param username Username (email)
	 * @return UserDetails object
	 */
	UserDetails loadUserByUsername(String username);

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
