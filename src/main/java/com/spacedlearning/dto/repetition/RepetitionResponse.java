// File: src/main/java/com/spacedlearning/dto/repetition/RepetitionResponse.java
package com.spacedlearning.dto.repetition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for repetition response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepetitionResponse {
	private UUID id;
	private UUID moduleProgressId;
	private RepetitionOrder repetitionOrder;
	private RepetitionStatus status;
	private LocalDate reviewDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}