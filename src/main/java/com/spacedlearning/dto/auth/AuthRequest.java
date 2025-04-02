package com.spacedlearning.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user authentication request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

	@NotBlank(message = "Username or email is required")
	private String usernameOrEmail;

	@NotBlank(message = "Password is required")
	private String password;
}