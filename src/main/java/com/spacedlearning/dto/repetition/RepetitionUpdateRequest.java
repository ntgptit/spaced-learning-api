// File: src/main/java/com/spacedlearning/dto/repetition/RepetitionUpdateRequest.java
package com.spacedlearning.dto.repetition;

import java.time.LocalDate;

import com.spacedlearning.entity.enums.RepetitionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating repetition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepetitionUpdateRequest {

    private RepetitionStatus status;
    private LocalDate reviewDate;
    private boolean rescheduleFollowing;
}
