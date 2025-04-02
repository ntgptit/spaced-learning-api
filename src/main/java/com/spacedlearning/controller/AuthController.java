package com.spacedlearning.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication API", description = "Endpoints for authentication")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "Login", description = "Authenticates a user with username or email and returns JWT tokens")
	public ResponseEntity<DataResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
		log.debug("REST request to login user with username or email: {}", request.getUsernameOrEmail());
		final AuthResponse authResponse = authService.authenticate(request);
		return ResponseEntity.ok(DataResponse.of(authResponse));
	}

	@PostMapping("/refresh-token")
	@Operation(summary = "Refresh token", description = "Refreshes an authentication token")
	public ResponseEntity<DataResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		log.debug("REST request to refresh token");
		final AuthResponse authResponse = authService.refreshToken(request);
		return ResponseEntity.ok(DataResponse.of(authResponse));
	}

	@PostMapping("/register")
	@Operation(summary = "Register user", description = "Registers a new user")
	public ResponseEntity<DataResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
		log.debug("REST request to register user with username: {} and email: {}", request.getUsername(),
				request.getEmail());
		final UserResponse registeredUser = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(registeredUser));
	}

	@GetMapping("/validate")
	@Operation(summary = "Validate token", description = "Validates a JWT token")
	public ResponseEntity<SuccessResponse> validateToken(@RequestParam String token) {
		log.debug("REST request to validate token");
		final boolean isValid = authService.validateToken(token);
		if (isValid) {
			return ResponseEntity.ok(SuccessResponse.of("Token is valid"));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(SuccessResponse.builder().message("Invalid token").success(false).build());
	}
}