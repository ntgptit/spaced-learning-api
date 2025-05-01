package com.spacedlearning.dto.repetition;

import com.spacedlearning.entity.enums.RepetitionStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for updating repetition completion status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepetitionCompletionRequest {
    @NotNull(message = "Status is required")
    private RepetitionStatus status;

    @DecimalMin(value = "0.0", message = "Score cannot be negative")
    @DecimalMax(value = "100.0", message = "Score cannot exceed 100")
    private BigDecimal score;
}