package com.spacedlearning.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Pattern(regexp = "^[a-zA-Z0-9._-]+$",
			message = "Username can only contain letters, numbers, dots, underscores and hyphens")
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;

	@NotBlank(message = "First name is required")
	private String firstName;

	@NotBlank(message = "Last name is required")
	private String lastName;

	public RegisterRequest(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}
}
