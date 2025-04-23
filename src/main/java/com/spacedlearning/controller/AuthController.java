package com.spacedlearning.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication API", description = "Endpoints for authentication")
public class AuthController {

    public static final String INVALID_TOKEN = "Invalid token";
    public static final String REGISTERS_A_NEW_USER = "Registers a new user";
    public static final String TOKEN_IS_VALID = "Token is valid";
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
    @Operation(summary = "Register user", description = REGISTERS_A_NEW_USER)
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
            return ResponseEntity.ok(SuccessResponse.of(TOKEN_IS_VALID));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(SuccessResponse.builder().message(INVALID_TOKEN).success(false).build());
    }
}