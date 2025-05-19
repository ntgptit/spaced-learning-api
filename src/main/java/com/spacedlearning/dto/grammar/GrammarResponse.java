// GrammarResponse.java
package com.spacedlearning.dto.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarResponse {
    private UUID id;
    private UUID moduleId;
    private String moduleName;
    private String title;
    private String explanation;
    private String usageNote;
    private String example;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}