package com.spacedlearning.security;

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom implementation of UserDetailsService. Loads user-specific data for
 * Spring Security.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Builds a UserDetails object from a User entity.
     *
     * @param user The user entity
     * @return A UserDetails object
     */
    private UserDetails buildUserDetails(User user) {
        // Create simple list with role USER
        final List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        // Create CustomUserDetails
        return new CustomUserDetails(user, authorities);
    }

	/**
     * Loads a user by username (email).
     *
     * @param username The username (email) to load
     * @return A UserDetails object
     * @throws UsernameNotFoundException if the user is not found
     * @throws SpacedLearningException if the user account is disabled
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Validate input
        Objects.requireNonNull(username, "Username cannot be null");

        log.debug("Loading user by username: {}", username);

        // Find user by email with roles (using optimized query to avoid N+1 issue)
        final User user = userRepository.findByEmailWithRoles(username).orElseThrow(() -> {
            log.warn("User not found with email: {}", username);
            return new UsernameNotFoundException("User not found with email: " + username);
        });

        // Check if user is active
        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            log.warn("User is inactive: {}", username);
            throw new SpacedLearningException("Account is disabled", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        // Create UserDetails
        return buildUserDetails(user);
    }
}