// VocabularyResponse.java
package com.spacedlearning.dto.vocabulary;

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
public class VocabularyResponse {
    private UUID id;
    private UUID moduleId;
    private String moduleName;
    private String term;
    private String definition;
    private String example;
    private String pronunciation;
    private String partOfSpeech;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}