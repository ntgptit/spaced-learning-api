// VocabularyUpdateRequest.java
package com.spacedlearning.dto.vocabulary;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyUpdateRequest {

    @Size(max = 100, message = "Term must not exceed 100 characters")
    private String term;

    private String definition;

    private String example;

    @Size(max = 100, message = "Pronunciation must not exceed 100 characters")
    private String pronunciation;

    @Size(max = 100, message = "Part of speech must not exceed 100 characters")
    private String partOfSpeech;
}