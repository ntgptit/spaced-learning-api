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
    private String grammarPattern;
    private String definition;
    private String structure;
    private String conjugation;
    private String examples;
    private String commonPhrases;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}