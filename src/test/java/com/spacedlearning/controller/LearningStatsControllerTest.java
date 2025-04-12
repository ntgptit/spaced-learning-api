package com.spacedlearning.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.spacedlearning.dto.stats.LearningInsightDTO;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;
import com.spacedlearning.entity.User;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.LearningStatsService;



class LearningStatsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LearningStatsService statsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private LearningStatsController learningStatsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(learningStatsController).build();
    }

    @Test
    void testGetDashboardStats_Success() throws Exception {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // Mock user and service response
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsernameOrEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        UserLearningStatsDTO stats = new UserLearningStatsDTO();
        when(statsService.getDashboardStats(user.getId())).thenReturn(stats);

        // Perform request
        mockMvc.perform(get("/api/v1/stats/dashboard").param("refreshCache", "false")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetDashboardStats_Unauthenticated() throws Exception {
        // Mock unauthenticated user
        SecurityContextHolder.clearContext();

        // Perform request
        mockMvc.perform(get("/api/v1/stats/dashboard").param("refreshCache", "false")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserDashboardStats_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        // Mock user existence and service response
        when(userRepository.existsById(userId)).thenReturn(true);
        UserLearningStatsDTO stats = new UserLearningStatsDTO();
        when(statsService.getDashboardStats(userId)).thenReturn(stats);

        // Perform request
        mockMvc.perform(get("/api/v1/stats/users/{userId}/dashboard", userId)
                .param("refreshCache", "false").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetUserDashboardStats_UserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        // Mock user not found
        when(userRepository.existsById(userId)).thenReturn(false);

        // Perform request
        mockMvc.perform(get("/api/v1/stats/users/{userId}/dashboard", userId)
                .param("refreshCache", "false").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetLearningInsights_Success() throws Exception {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // Mock user and service response
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsernameOrEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        List<LearningInsightDTO> insights = List.of(new LearningInsightDTO());
        when(statsService.getLearningInsights(user.getId())).thenReturn(insights);

        // Perform request
        mockMvc.perform(get("/api/v1/stats/insights").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetLearningInsights_Unauthenticated() throws Exception {
        // Mock unauthenticated user
        SecurityContextHolder.clearContext();

        // Perform request
        mockMvc.perform(get("/api/v1/stats/insights").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserLearningInsights_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        // Mock user existence and service response
        when(userRepository.existsById(userId)).thenReturn(true);
        List<LearningInsightDTO> insights = List.of(new LearningInsightDTO());
        when(statsService.getLearningInsights(userId)).thenReturn(insights);

        // Perform request
        mockMvc.perform(get("/api/v1/stats/users/{userId}/insights", userId)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetUserLearningInsights_UserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        // Mock user not found
        when(userRepository.existsById(userId)).thenReturn(false);

        // Perform request
        mockMvc.perform(get("/api/v1/stats/users/{userId}/insights", userId)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void testGetDashboardStats_UserNotFound() throws Exception {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // Mock user not found
        when(userRepository.findByUsernameOrEmail("test@example.com")).thenReturn(Optional.empty());
        when(messageSource.getMessage("error.resource.notfound",
                new Object[] {"User", "test@example.com"}, "User not found",
                LocaleContextHolder.getLocale())).thenReturn("User not found");

        // Perform request
        mockMvc.perform(get("/api/v1/stats/dashboard").param("refreshCache", "false")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserDashboardStats_AccessDenied() throws Exception {
        UUID userId = UUID.randomUUID();

        // Mock access denied
        when(messageSource.getMessage("error.resource.notfound", new Object[] {"User", userId},
                "User not found", LocaleContextHolder.getLocale())).thenReturn("Access denied");

        // Perform request
        mockMvc.perform(get("/api/v1/stats/users/{userId}/dashboard", userId)
                .param("refreshCache", "false").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetLearningInsights_UserNotFound() throws Exception {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // Mock user not found
        when(userRepository.findByUsernameOrEmail("test@example.com")).thenReturn(Optional.empty());
        when(messageSource.getMessage("error.resource.notfound",
                new Object[] {"User", "test@example.com"}, "User not found",
                LocaleContextHolder.getLocale())).thenReturn("User not found");

        // Perform request
        mockMvc.perform(get("/api/v1/stats/insights").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserLearningInsights_AccessDenied() throws Exception {
        UUID userId = UUID.randomUUID();

        // Mock access denied
        when(messageSource.getMessage("error.resource.notfound", new Object[] {"User", userId},
                "User not found", LocaleContextHolder.getLocale())).thenReturn("Access denied");

        // Perform request
        mockMvc.perform(get("/api/v1/stats/users/{userId}/insights", userId)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

}
