package com.spacedlearning.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookStatsResponse {
    private String bookName;
    private int totalModules;
    private int completedModules;
    private int activeModules;
    private int totalWords;
    private int learnedWords;
    private double completionPercentage;
}