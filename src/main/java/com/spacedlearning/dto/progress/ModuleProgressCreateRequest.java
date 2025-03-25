// File: src/main/java/com/spacedlearning/dto/progress/ModuleProgressCreateRequest.java
package com.spacedlearning.dto.progress;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.spacedlearning.entity.enums.CycleStudied;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating module progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressCreateRequest {

	@NotNull(message = "Module ID is required")
	private UUID moduleId;

	@NotNull(message = "User ID is required")
	private UUID userId;

	private LocalDate firstLearningDate;

	private CycleStudied cyclesStudied;

	private LocalDate nextStudyDate;

	@DecimalMin(value = "0.00", message = "Percent complete cannot be negative")
	@DecimalMax(value = "100.00", message = "Percent complete cannot exceed 100")
	private BigDecimal percentComplete;
}