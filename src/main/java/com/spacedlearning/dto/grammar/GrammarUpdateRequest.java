package com.spacedlearning.dto.grammar;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarUpdateRequest {

    @Size(max = 100, message = "Grammar pattern must not exceed 100 characters")
    private String grammarPattern;

    private String definition;
    private String structure;
    private String conjugation;
    private String examples;
    private String commonPhrases;
    private String notes;
}