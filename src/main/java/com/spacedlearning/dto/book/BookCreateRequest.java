// File: src/main/java/com/spacedlearning/dto/book/BookCreateRequest.java
package com.spacedlearning.dto.book;

import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new book
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateRequest {

	@NotBlank(message = "Book name is required")
	@Size(max = 100, message = "Book name must not exceed 100 characters")
	private String name;

	private String description;

	private BookStatus status;

	private DifficultyLevel difficultyLevel;

	@Size(max = 50, message = "Category must not exceed 50 characters")
	private String category;
}