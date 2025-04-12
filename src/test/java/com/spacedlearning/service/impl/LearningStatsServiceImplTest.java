package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.spacedlearning.dto.stats.LearningInsightDTO;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;
import com.spacedlearning.entity.enums.InsightType;
import com.spacedlearning.service.DashboardStatsService;



class LearningStatsServiceImplTest {

    @Mock
    private DashboardStatsService dashboardStatsService;

    @InjectMocks
    private LearningStatsServiceImpl learningStatsService;

    private UUID userId;
    private UserLearningStatsDTO mockStats;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        mockStats = UserLearningStatsDTO.builder().weeklyNewWordsRate(new BigDecimal("25.5"))
                .streakDays(5).pendingWords(10).dueToday(3).build();
    }

    @Test
    void testGetDashboardStats() {
        when(dashboardStatsService.getDashboardStats(userId)).thenReturn(mockStats);

        UserLearningStatsDTO result = learningStatsService.getDashboardStats(userId);

        assertEquals(mockStats, result);
    }

    @Test
    void testGetLearningInsights() {
        when(dashboardStatsService.getDashboardStats(userId)).thenReturn(mockStats);

        List<LearningInsightDTO> insights = learningStatsService.getLearningInsights(userId);

        assertEquals(4, insights.size());

        assertEquals(InsightType.VOCABULARY_RATE, insights.get(0).getType());
        assertEquals("You learn 25.5% new vocabulary each week", insights.get(0).getMessage());

        assertEquals(InsightType.STREAK, insights.get(1).getType());
        assertEquals("Your current streak is 5 days - keep going!", insights.get(1).getMessage());

        assertEquals(InsightType.PENDING_WORDS, insights.get(2).getType());
        assertEquals("You have 10 words pending to learn", insights.get(2).getMessage());

        assertEquals(InsightType.DUE_TODAY, insights.get(3).getType());
        assertEquals("Complete today's 3 sessions to maintain your streak",
                insights.get(3).getMessage());
    }
}
