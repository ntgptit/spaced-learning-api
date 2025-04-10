package com.spacedlearning.dto.learning;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningModuleResponse {
    private String bookName;
    private Integer bookNo;
    private String moduleTitle;
    private Integer moduleNo;
    private Integer moduleWordCount;
    private String progressCyclesStudied;
    private LocalDate progressNextStudyDate;
    private LocalDate progressFirstLearningDate;
    private Integer progressLatestPercentComplete;
    private Integer progressDueTaskCount;
    private String moduleId;
    private List<String> studyHistory;

}