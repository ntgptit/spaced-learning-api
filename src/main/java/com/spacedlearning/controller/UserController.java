// File: src/main/java/com/spacedlearning/controller/UserController.java
package com.spacedlearning.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.PageResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.service.UserService;
import com.spacedlearning.util.PageUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for User operations
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {

	private final UserService userService;

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete user", description = "Deletes a user by ID")
	public ResponseEntity<SuccessResponse> deleteUser(@PathVariable UUID id) {
		log.debug("REST request to delete user with ID: {}", id);
		userService.delete(id);
		return ResponseEntity.ok(SuccessResponse.of("User deleted successfully"));
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all users", description = "Retrieves a paginated list of all users")
	public ResponseEntity<PageResponse<UserDetailedResponse>> getAllUsers(
			@PageableDefault(size = 20) Pageable pageable) {
		log.debug("REST request to get all users, pageable: {}", pageable);
		final Page<UserDetailedResponse> page = userService.findAll(pageable);
		return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
	}

	@GetMapping("/me")
	@Operation(summary = "Get current user", description = "Retrieves the current authenticated user")
	public ResponseEntity<DataResponse<UserResponse>> getCurrentUser() {
		log.debug("REST request to get current user");
		final UserResponse user = userService.getCurrentUser();
		return ResponseEntity.ok(DataResponse.of(user));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
	@Operation(summary = "Get user by ID", description = "Retrieves a user by ID")
	public ResponseEntity<DataResponse<UserDetailedResponse>> getUserById(@PathVariable UUID id) {
		log.debug("REST request to get user with ID: {}", id);
		final UserDetailedResponse user = userService.findById(id);
		return ResponseEntity.ok(DataResponse.of(user));
	}

	@PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore user", description = "Restores a soft-deleted user")
    public ResponseEntity<DataResponse<UserResponse>> restoreUser(@PathVariable UUID id) {
        log.debug("REST request to restore user with ID: {}", id);
        final UserResponse restoredUser = userService.restore(id);
        return ResponseEntity.ok(DataResponse.of(restoredUser));
    }

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
	@Operation(summary = "Update user", description = "Updates an existing user")
	public ResponseEntity<DataResponse<UserResponse>> updateUser(@PathVariable UUID id,
			@Valid @RequestBody UserUpdateRequest request) {
		log.debug("REST request to update user with ID: {}, request: {}", id, request);
		final UserResponse updatedUser = userService.update(id, request);
		return ResponseEntity.ok(DataResponse.of(updatedUser));
	}
}