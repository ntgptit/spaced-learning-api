package com.spacedlearning.dto.learning;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.spacedlearning.entity.enums.CycleStudied;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningModuleResponse {
    private UUID id;
    private String bookName;
    private String title;
    private int wordCount;
    private CycleStudied cyclesStudied;
    private LocalDate firstLearningDate;
    private int percentComplete;
    private LocalDate nextStudyDate;
    private int pendingRepetitions;
    private int learnedWords;
    private LocalDateTime lastStudyDate;
    private List<LocalDate> studyHistory;
}