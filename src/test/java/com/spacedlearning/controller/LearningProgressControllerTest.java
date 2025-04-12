package com.spacedlearning.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.service.LearningProgressService;



@WebMvcTest(LearningProgressController.class)
class LearningProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private LearningProgressService learningProgressService;

    @InjectMocks
    private LearningProgressController learningProgressController;

    @Test
    void testGetDashboardStats() throws Exception {
        DashboardStatsResponse statsResponse = new DashboardStatsResponse();
        when(learningProgressService.getDashboardStats(anyString(), any()))
                .thenReturn(statsResponse);

        mockMvc.perform(get("/api/v1/learning/dashboard-stats").param("book", "testBook")
                .param("date", "2023-01-01")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getDashboardStats("testBook", LocalDate.of(2023, 1, 1));
    }

    @Test
    void testGetAllModules() throws Exception {
        List<LearningModuleResponse> modules = Collections.emptyList();
        when(learningProgressService.getAllModules()).thenReturn(modules);

        mockMvc.perform(get("/api/v1/learning/modules")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getAllModules();
    }

    @Test
    void testGetDueModules() throws Exception {
        List<LearningModuleResponse> modules = Collections.emptyList();
        when(learningProgressService.getDueModules(anyInt())).thenReturn(modules);

        mockMvc.perform(get("/api/v1/learning/modules/due").param("daysThreshold", "7"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getDueModules(7);
    }

    @Test
    void testGetCompletedModules() throws Exception {
        List<LearningModuleResponse> modules = Collections.emptyList();
        when(learningProgressService.getCompletedModules()).thenReturn(modules);

        mockMvc.perform(get("/api/v1/learning/modules/completed")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getCompletedModules();
    }

    @Test
    void testGetUniqueBooks() throws Exception {
        List<String> books = Collections.emptyList();
        when(learningProgressService.getUniqueBooks()).thenReturn(books);

        mockMvc.perform(get("/api/v1/learning/books")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getUniqueBooks();
    }

    @Test
    void testGetBookStats() throws Exception {
        BookStatsResponse statsResponse = new BookStatsResponse();
        when(learningProgressService.getBookStats(anyString())).thenReturn(statsResponse);

        mockMvc.perform(get("/api/v1/learning/books/testBook/stats")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getBookStats("testBook");
    }

    @Test
    void testExportData() throws Exception {
        Map<String, String> exportResult = Collections.singletonMap("filePath", "testPath");
        when(learningProgressService.exportData()).thenReturn(exportResult);

        mockMvc.perform(post("/api/v1/learning/export").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.filePath").value("testPath"));

        verify(learningProgressService).exportData();
    }

    @Test
    void testGetDashboardStatsWithNoParams() throws Exception {
        DashboardStatsResponse statsResponse = new DashboardStatsResponse();
        when(learningProgressService.getDashboardStats(null, null)).thenReturn(statsResponse);

        mockMvc.perform(get("/api/v1/learning/dashboard-stats")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getDashboardStats(null, null);
    }

    @Test
    void testGetDueModulesWithDefaultThreshold() throws Exception {
        List<LearningModuleResponse> modules = Collections.emptyList();
        when(learningProgressService.getDueModules(7)).thenReturn(modules);

        mockMvc.perform(get("/api/v1/learning/modules/due")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(learningProgressService).getDueModules(7);
    }

    @Test
    void testGetBookStatsWithInvalidBook() throws Exception {
        when(learningProgressService.getBookStats(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid book"));

        mockMvc.perform(get("/api/v1/learning/books/invalidBook/stats"))
                .andExpect(status().isBadRequest());

        verify(learningProgressService).getBookStats("invalidBook");
    }
}
