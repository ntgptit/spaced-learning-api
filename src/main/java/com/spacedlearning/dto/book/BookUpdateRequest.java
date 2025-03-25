// File: src/main/java/com/spacedlearning/dto/book/BookUpdateRequest.java
package com.spacedlearning.dto.book;

import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing book
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {

	@Size(max = 100, message = "Book name must not exceed 100 characters")
	private String name;

	private String description;

	private BookStatus status;

	private DifficultyLevel difficultyLevel;

	@Size(max = 50, message = "Category must not exceed 50 characters")
	private String category;
}