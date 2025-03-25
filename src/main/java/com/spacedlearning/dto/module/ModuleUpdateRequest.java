// File: src/main/java/com/spacedlearning/dto/module/ModuleUpdateRequest.java
package com.spacedlearning.dto.module;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing module
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleUpdateRequest {

	@Min(value = 1, message = "Module number must be at least 1")
	private Integer moduleNo;

	@Size(max = 255, message = "Title must not exceed 255 characters")
	private String title;

	@Min(value = 0, message = "Word count cannot be negative")
	private Integer wordCount;
}