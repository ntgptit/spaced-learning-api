// File: src/main/java/com/spacedlearning/dto/module/ModuleDetailResponse.java
package com.spacedlearning.dto.module;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for detailed module response including progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDetailResponse {
	private UUID id;
	private UUID bookId;
	private String bookName;
	private Integer moduleNo;
	private String title;
	private Integer wordCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<ModuleProgressSummaryResponse> progress;
}