package com.spacedlearning.mapper;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.entity.Role;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.security.CustomUserDetailsService;

import lombok.extern.slf4j.Slf4j;

/**
 * Mapper for User entity and DTOs
 */
@Component
@Slf4j
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
	private final CustomUserDetailsService userDetailsService;

	/**
	 * Constructor with @Lazy annotation to break circular dependency
	 * 
	 * @param passwordEncoder    Password encoder
	 * @param userDetailsService User details service (lazy-loaded)
	 */
	public UserMapper(PasswordEncoder passwordEncoder, @Lazy CustomUserDetailsService userDetailsService) {
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}

    /**
	 * Convert entity to DTO
	 *
	 * @param user The User entity
	 * @return UserResponse DTO
	 */
	public UserResponse toDto(User user) {
		if (user == null) {
			return null;
		}

		// Split name into first and last name if possible
		String firstName = "";
		String lastName = "";

		if (user.getName() != null && !user.getName().trim().isEmpty()) {
			final String[] nameParts = user.getName().trim().split("\\s+", 2);
			firstName = nameParts[0];
			lastName = nameParts.length > 1 ? nameParts[1] : "";
        }

		return UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail())
				.firstName(firstName).lastName(lastName).displayName(user.getName()).createdAt(user.getCreatedAt())
				.roles(user.getRoles() != null
						? user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
						: null)
				.build();
    }

    /**
	 * Convert entity to detailed DTO
	 *
	 * @param user The User entity
	 * @return UserDetailedResponse DTO
	 */
	public UserDetailedResponse toDetailedDto(User user) {
		if (user == null) {
            return null;
        }

		return UserDetailedResponse.builder().id(user.getId()).email(user.getEmail()).displayName(user.getName())
				.createdAt(user.getCreatedAt()).updatedAt(user.getUpdatedAt()).deletedAt(user.getDeletedAt()).build();
    }

    /**
	 * Convert RegisterRequest to User entity
	 *
	 * @param request The RegisterRequest DTO
	 * @return User entity
	 */
	public User registerRequestToEntity(RegisterRequest request) {
		if (request == null) {
			return null;
		}

		final User user = new User();
		user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

		// Combine first and last name for display name
		final StringBuilder displayName = new StringBuilder().append(request.getFirstName());
		if (request.getLastName() != null && !request.getLastName().isEmpty()) {
			displayName.append(" ").append(request.getLastName());
		}
		user.setName(displayName.toString());

		// Set default status
		user.setStatus(UserStatus.ACTIVE);

        return user;
    }

    /**
	 * Update User entity from UserUpdateRequest
	 *
	 * @param request The update request
	 * @param user    The user entity to update
	 */
	public void updateFromDto(UserUpdateRequest request, User user) {
		if (request == null || user == null) {
			return;
        }

		if (request.getDisplayName() != null) {
			user.setName(request.getDisplayName());
		}

		if (request.getPassword() != null && !request.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}
    }

    /**
	 * Load UserDetails for a user by username
	 *
	 * @param username Username (email)
	 * @return UserDetails object
	 */
	public UserDetails loadUserByUsername(String username) {
		try {
			// Delegate to the UserDetailsService for consistency
			return userDetailsService.loadUserByUsername(username);
		} catch (final Exception e) {
			log.error("Error loading user details for username: {}, Error: {}", username, e.getMessage());
			throw e; // Rethrow to maintain exception flow
        }
    }
}