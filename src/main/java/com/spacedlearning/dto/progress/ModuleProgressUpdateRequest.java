// File: src/main/java/com/spacedlearning/dto/progress/ModuleProgressUpdateRequest.java
package com.spacedlearning.dto.progress;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.spacedlearning.entity.enums.CycleStudied;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating module progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressUpdateRequest {

	private LocalDate firstLearningDate;

	private CycleStudied cyclesStudied;

	private LocalDate nextStudyDate;

	@DecimalMin(value = "0.00", message = "Percent complete cannot be negative")
	@DecimalMax(value = "100.00", message = "Percent complete cannot exceed 100")
	private BigDecimal percentComplete;
}