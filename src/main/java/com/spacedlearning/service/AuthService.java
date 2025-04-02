package com.spacedlearning.service;

import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserResponse;

/**
 * Service interface for Authentication operations
 */
public interface AuthService {

	/**
	 * Authenticate a user
	 * 
	 * @param request Auth request containing username/email and password
	 * @return Auth response with tokens and user information
	 */
	AuthResponse authenticate(AuthRequest request);

	/**
	 * Extract username from token
	 * 
	 * @param token JWT token
	 * @return Username or email
	 */
	String getUsernameFromToken(String token);

	/**
	 * Refresh an authentication token
	 * 
	 * @param request Refresh token request
	 * @return Auth response with new tokens
	 */
	AuthResponse refreshToken(RefreshTokenRequest request);

	/**
	 * Register a new user
	 * 
	 * @param request Register request
	 * @return User response
	 */
	UserResponse register(RegisterRequest request);

	/**
	 * Validate an authentication token
	 * 
	 * @param token JWT token
	 * @return true if valid, false otherwise
	 */
	boolean validateToken(String token);
}