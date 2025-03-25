// File: src/main/java/com/spacedlearning/dto/progress/ModuleProgressSummaryResponse.java
package com.spacedlearning.dto.progress;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.spacedlearning.entity.enums.CycleStudied;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for summarized module progress response (without repetitions)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressSummaryResponse {
    private UUID id;
    private UUID moduleId;
    private UUID userId;
    private LocalDate firstLearningDate;
    private CycleStudied cyclesStudied;
    private LocalDate nextStudyDate;
    private BigDecimal percentComplete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	private int repetitionCount;
}