// VocabularyCreateRequest.java
package com.spacedlearning.dto.vocabulary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyCreateRequest {

    @NotNull(message = "Module ID is required")
    private UUID moduleId;

    @NotBlank(message = "Term is required")
    @Size(max = 100, message = "Term must not exceed 100 characters")
    private String term;

    private String definition;

    private String example;

    @Size(max = 100, message = "Pronunciation must not exceed 100 characters")
    private String pronunciation;

    @NotBlank(message = "Part of speech is required")
    @Size(max = 100, message = "Part of speech must not exceed 100 characters")
    private String partOfSpeech;
}