// File: src/main/java/com/spacedlearning/dto/book/BookSummaryResponse.java
package com.spacedlearning.dto.book;

import java.time.LocalDateTime;
import java.util.UUID;

import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for summarized book response (without modules)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryResponse {
	private UUID id;
	private String name;
	private BookStatus status;
	private DifficultyLevel difficultyLevel;
	private String category;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int moduleCount;
}