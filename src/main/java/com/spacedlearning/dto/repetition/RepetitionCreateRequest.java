// File: src/main/java/com/spacedlearning/dto/repetition/RepetitionCreateRequest.java
package com.spacedlearning.dto.repetition;

import java.time.LocalDate;
import java.util.UUID;

import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating repetition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepetitionCreateRequest {

	@NotNull(message = "Module progress ID is required")
	private UUID moduleProgressId;

	@NotNull(message = "Repetition order is required")
	private RepetitionOrder repetitionOrder;

	private RepetitionStatus status;

	private LocalDate reviewDate;
}