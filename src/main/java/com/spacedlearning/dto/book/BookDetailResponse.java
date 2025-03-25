// File: src/main/java/com/spacedlearning/dto/book/BookDetailResponse.java
package com.spacedlearning.dto.book;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for detailed book response including modules
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {
	private UUID id;
	private String name;
	private String description;
	private BookStatus status;
	private DifficultyLevel difficultyLevel;
	private String category;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<ModuleDetailResponse> modules;
}