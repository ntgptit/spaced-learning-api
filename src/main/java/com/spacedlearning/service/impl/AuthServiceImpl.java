package com.spacedlearning.service.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
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

	private static final String DEFAULT_ROLE = "ROLE_USER";

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final MessageSource messageSource;

	@Override
	@Transactional(readOnly = true)
	public AuthResponse authenticate(final AuthRequest request) {
		Objects.requireNonNull(request, "Auth request must not be null");
		Objects.requireNonNull(request.getEmail(), "Email must not be null");
		Objects.requireNonNull(request.getPassword(), "Password must not be null");

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
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, "resource.user",
						userDetails.getUsername()));

		final UserResponse userResponse = userMapper.toDto(user);

		log.info("User authenticated successfully: {}", userDetails.getUsername());
		return AuthResponse.builder().token(accessToken).refreshToken(refreshToken).user(userResponse).build();
	}

	@Override
	@Transactional(readOnly = true)
	public String getUsernameFromToken(final String token) {
		if (StringUtils.isBlank(token)) {
			throw SpacedLearningException.validationError(messageSource, "error.auth.invalidToken");
		}

		try {
			return tokenProvider.getUsernameFromToken(token);
		} catch (final JwtException e) {
			log.error("Failed to extract username from token: {}", e.getMessage());
			throw SpacedLearningException.forbidden(messageSource, "error.auth.invalidToken");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AuthResponse refreshToken(final RefreshTokenRequest request) {
		Objects.requireNonNull(request, "Refresh token request must not be null");
		Objects.requireNonNull(request.getRefreshToken(), "Refresh token must not be null");

		log.debug("Refreshing token");

		try {
			// Validate refresh token
			if (!tokenProvider.validateToken(request.getRefreshToken())
					|| !tokenProvider.isRefreshToken(request.getRefreshToken())) {
				throw SpacedLearningException.forbidden(messageSource, "error.auth.invalidToken");
			}

			// Extract username and load user
			final String username = tokenProvider.getUsernameFromToken(request.getRefreshToken());
			final User user = userRepository.findByEmail(username).orElseThrow(
					() -> SpacedLearningException.resourceNotFound(messageSource, "resource.user", username));

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
			throw SpacedLearningException.forbidden(messageSource, "error.auth.invalidToken");
		}
	}

	@Override
    @Transactional
	public UserResponse register(final RegisterRequest request) {
		Objects.requireNonNull(request, "Register request must not be null");
		Objects.requireNonNull(request.getEmail(), "Email must not be null");
		Objects.requireNonNull(request.getPassword(), "Password must not be null");

        log.debug("Registering new user with email: {}", request.getEmail());

        // Check if email is already in use
        if (userRepository.existsByEmail(request.getEmail())) {
            throw SpacedLearningException.resourceAlreadyExists(
					messageSource, "resource.user", "email", request.getEmail());
        }

        // Create new user
        final User user = userMapper.registerRequestToEntity(request);

        // Assign default role
		final Role userRole = roleRepository.findByName(DEFAULT_ROLE)
				.orElseThrow(() -> new SpacedLearningException(
						messageSource.getMessage("error.role.defaultNotFound", null, "Default role not found",
								LocaleContextHolder.getLocale()),
						new RuntimeException("Default role not found"), HttpStatus.INTERNAL_SERVER_ERROR));

        user.addRole(userRole);

        final User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

	@Override
	@Transactional(readOnly = true)
	public boolean validateToken(final String token) {
		if (StringUtils.isBlank(token)) {
			return false;
		}

		try {
			return tokenProvider.validateToken(token);
		} catch (final JwtException e) {
			log.debug("Token validation failed: {}", e.getMessage());
			return false;
		}
	}
}