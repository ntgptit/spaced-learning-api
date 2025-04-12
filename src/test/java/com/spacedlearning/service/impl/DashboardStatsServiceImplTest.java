package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.UserStatistics;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.repository.UserStatisticsRepository;



class DashboardStatsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatisticsRepository statsRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DashboardStatsServiceImpl dashboardStatsService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
    }

    @Test
    void testGetDashboardStats_UserExistsWithStats() {
        // Arrange
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setStreakDays(5);
        userStatistics.setStreakWeeks(2);
        userStatistics.setLongestStreakDays(10);
        userStatistics.setLastStatisticsUpdate(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(statsRepository.findByUserId(userId)).thenReturn(Optional.of(userStatistics));

        // Act
        UserLearningStatsDTO result = dashboardStatsService.getDashboardStats(userId);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getStreakDays());
        assertEquals(2, result.getStreakWeeks());
        assertEquals(10, result.getLongestStreakDays());
        verify(userRepository).findById(userId);
        verify(statsRepository).findByUserId(userId);
    }

    @Test
    void testGetDashboardStats_UserExistsWithoutStats() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(statsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleRepository.getTotalWordCount()).thenReturn(100);
        when(moduleRepository.getLearnedWordCount()).thenReturn(50);

        // Act
        UserLearningStatsDTO result = dashboardStatsService.getDashboardStats(userId);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getTotalWords());
        assertEquals(50, result.getLearnedWords());
        assertEquals(BigDecimal.valueOf(50.00), result.getVocabularyCompletionRate());
        verify(userRepository).findById(userId);
        verify(statsRepository).findByUserId(userId);
        verify(moduleRepository).getTotalWordCount();
        verify(moduleRepository).getLearnedWordCount();
    }

    @Test
    void testGetDashboardStats_UserDoesNotExist() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("User not found");

        // Act & Assert
        SpacedLearningException exception = assertThrows(SpacedLearningException.class,
                () -> dashboardStatsService.getDashboardStats(userId));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetDashboardStats_NullUserId() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> dashboardStatsService.getDashboardStats(null));
        assertEquals("User ID must not be null", exception.getMessage());
    }
}
