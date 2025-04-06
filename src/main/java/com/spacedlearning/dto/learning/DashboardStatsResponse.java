package com.spacedlearning.dto.learning;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private List<LearningModuleResponse> modules;
    private int totalModules;
    private int dueModules;
    private int completedModules;
    private int activeModules;
    private int dueTodayCount;
    private int dueThisWeekCount;
    private int dueThisMonthCount;
}