package com.spacedlearning.dto.auth;

import com.spacedlearning.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response containing JWT token and user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

	public AuthResponse(String string, String string2) {
		// TODO Auto-generated constructor stub
	}

	private String token;
	private String refreshToken;
	private UserResponse user;
}
