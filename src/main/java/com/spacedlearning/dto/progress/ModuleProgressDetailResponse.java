// File: src/main/java/com/spacedlearning/dto/progress/ModuleProgressDetailResponse.java
package com.spacedlearning.dto.progress;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.entity.enums.CycleStudied;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for detailed module progress response including repetitions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressDetailResponse {
	private UUID id;
	private UUID moduleId;
	private String moduleTitle;
	private UUID userId;
	private String userName;
	private LocalDate firstLearningDate;
	private CycleStudied cyclesStudied;
	private LocalDate nextStudyDate;
	private BigDecimal percentComplete;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<RepetitionResponse> repetitions;
}