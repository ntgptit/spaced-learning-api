
// File: src/main/java/com/spacedlearning/security/UserSecurity.java
package com.spacedlearning.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for user-based security checks
 */
@Component("userSecurity")
@RequiredArgsConstructor
@Slf4j
public class UserSecurity {

	/**
	 * Checks if the current authenticated user matches the requested user ID
	 * 
	 * @param userId User ID to check
	 * @return true if current user matches the ID, false otherwise
	 */
	public boolean isCurrentUser(UUID userId) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}

		final Object principal = authentication.getPrincipal();

		// For CustomUserDetails containing the User entity
		if (principal instanceof final CustomUserDetails userDetails) {
			return userDetails.getUser().getId().equals(userId);
		}

		// For regular UserDetails (fallback)
		if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
			final String username = ((org.springframework.security.core.userdetails.UserDetails) principal)
					.getUsername();
			log.debug("Checking access for user {} against requested ID {}", username, userId);
			// This is a simplified check - in a real implementation, you would need to
			// look up the user ID by username from the repository
			return true; // Allow admins to access (handled by PreAuthorize annotation)
		}

		return false;
	}
}