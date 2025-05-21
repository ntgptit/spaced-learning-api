package com.spacedlearning.dto.grammar;

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
public class GrammarCreateRequest {

    @NotNull(message = "Module ID is required")
    private UUID moduleId;

    @NotBlank(message = "Grammar pattern is required")
    @Size(max = 100, message = "Grammar pattern must not exceed 100 characters")
    private String grammarPattern;

    private String definition;
    private String structure;
    private String conjugation;
    private String examples;
    private String commonPhrases;
    private String notes;
}