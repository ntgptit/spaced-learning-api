package com.spacedlearning.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.exception.ApiError;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter for JWT authentication. Processes login requests and generates JWT
 * tokens upon successful authentication.
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
			ObjectMapper objectMapper) {
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
		this.objectMapper = objectMapper;
		setFilterProcessesUrl("/api/v1/auth/login");
	}

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        try {
            final AuthRequest authRequest = objectMapper.readValue(request.getInputStream(), AuthRequest.class);
            Objects.requireNonNull(authRequest, "Authentication request cannot be null");
            Objects.requireNonNull(authRequest.getEmail(), "Email cannot be null");
            Objects.requireNonNull(authRequest.getPassword(), "Password cannot be null");

            log.debug("Attempting authentication for user: {}", authRequest.getEmail());

            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword());

            return authenticationManager.authenticate(authentication);
        } catch (final IOException e) {
            log.error("Failed to parse authentication request", e);
            throw new AuthenticationServiceException("Failed to parse authentication request", e);
        } catch (final NullPointerException e) {
            log.error("Invalid authentication request: {}", e.getMessage());
            throw new AuthenticationServiceException("Invalid authentication request: " + e.getMessage(), e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        final UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        log.info("Authentication successful for user: {}", userDetails.getUsername());

        final String token = tokenProvider.generateToken(authResult);
		final String refreshToken = tokenProvider.generateRefreshToken(authResult);

		// Create basic user response without UserMapper
		final UserResponse userResponse = UserResponse.builder().email(userDetails.getUsername()).roles(
				userDetails.getAuthorities().stream().map(auth -> auth.getAuthority().replace("ROLE_", "")).toList())
				.build();

		final AuthResponse authResponse = AuthResponse.builder().token(token).refreshToken(refreshToken)
				.user(userResponse).build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(response.getOutputStream(), authResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        log.warn("Authentication failed: {}", failed.getMessage());

		final ApiError errorResponse = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .message("Authentication failed: " + failed.getMessage())
            .path(request.getRequestURI())
            .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}