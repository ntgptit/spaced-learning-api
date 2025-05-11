package com.spacedlearning.dto.module;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for summarized module response (without progress)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleSummaryResponse {
    private UUID id;
    private UUID bookId;
    private Integer moduleNo;
    private String title;
    private Integer wordCount;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}