// File: src/main/java/com/spacedlearning/dto/module/ModuleCreateRequest.java
package com.spacedlearning.dto.module;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new module
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleCreateRequest {

	@NotNull(message = "Book ID is required")
	private UUID bookId;

	@NotNull(message = "Module number is required")
	@Min(value = 1, message = "Module number must be at least 1")
	private Integer moduleNo;

	@NotBlank(message = "Title is required")
	@Size(max = 255, message = "Title must not exceed 255 characters")
	private String title;

	@Min(value = 0, message = "Word count cannot be negative")
	private Integer wordCount;
}