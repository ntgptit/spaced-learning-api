package com.spacedlearning.dto.repetition;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for rescheduling repetition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepetitionRescheduleRequest {
    @NotNull(message = "Review date is required")
    private LocalDate reviewDate;

    private boolean rescheduleFollowing;
}