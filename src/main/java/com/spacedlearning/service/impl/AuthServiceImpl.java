// File: src/main/java/com/spacedlearning/service/impl/AuthServiceImpl.java
package com.spacedlearning.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.entity.Role;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.repository.RoleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.security.JwtTokenProvider;
import com.spacedlearning.service.AuthService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final MessageSource messageSource;

	@Override
	@Transactional(readOnly = true)
	public AuthResponse authenticate(AuthRequest request) {
		log.debug("Authenticating user with email: {}", request.getEmail());

		// Authenticate user
		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Generate tokens
		final String accessToken = tokenProvider.generateToken(authentication);
		final String refreshToken = tokenProvider.generateRefreshToken(authentication);

		// Get user details
		final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		final User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("User", userDetails.getUsername()));

		final UserResponse userResponse = userMapper.toDto(user);

		log.info("User authenticated successfully: {}", userDetails.getUsername());
		return AuthResponse.builder().token(accessToken).refreshToken(refreshToken).user(userResponse).build();
	}

	@Override
	@Transactional(readOnly = true)
	public String getUsernameFromToken(String token) {
		try {
			return tokenProvider.getUsernameFromToken(token);
		} catch (final JwtException e) {
			log.error("Failed to extract username from token: {}", e.getMessage());
			throw SpacedLearningException.forbidden("Invalid token");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AuthResponse refreshToken(RefreshTokenRequest request) {
		log.debug("Refreshing token");

		try {
			// Validate refresh token
			if (!tokenProvider.validateToken(request.getRefreshToken())
					|| !tokenProvider.isRefreshToken(request.getRefreshToken())) {
				throw SpacedLearningException.forbidden("Invalid refresh token");
			}

			// Extract username and load user
			final String username = tokenProvider.getUsernameFromToken(request.getRefreshToken());
			final User user = userRepository.findByEmail(username)
					.orElseThrow(() -> SpacedLearningException.resourceNotFound("User", username));

			// Create authentication object
			final UserDetails userDetails = userMapper.loadUserByUsername(username);
			final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());

			// Generate new tokens
			final String accessToken = tokenProvider.generateToken(authentication);
			final String refreshToken = tokenProvider.generateRefreshToken(authentication);

			final UserResponse userResponse = userMapper.toDto(user);

			log.info("Token refreshed successfully for user: {}", username);
			return AuthResponse.builder().token(accessToken).refreshToken(refreshToken).user(userResponse).build();

		} catch (final JwtException e) {
			log.error("Failed to refresh token: {}", e.getMessage());
			throw SpacedLearningException.forbidden("Invalid refresh token");
		}
	}

	@Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());

        // Check if email is already in use
        if (userRepository.existsByEmail(request.getEmail())) {
            throw SpacedLearningException.resourceAlreadyExists(
                    messageSource, "user", "email", request.getEmail());
        }

        // Create new user
        final User user = userMapper.registerRequestToEntity(request);

        // Assign default role
        final Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.addRole(userRole);

        final User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

	@Override
	@Transactional(readOnly = true)
	public boolean validateToken(String token) {
		try {
			return tokenProvider.validateToken(token);
		} catch (final JwtException e) {
			log.debug("Token validation failed: {}", e.getMessage());
			return false;
		}
	}
}