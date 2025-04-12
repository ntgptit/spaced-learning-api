package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.entity.Book;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.custom.LearningModuleRepository;



class LearningProgressServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LearningModuleRepository learningModuleRepository;

    @InjectMocks
    private LearningProgressServiceImpl learningProgressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardStats() {
        List<LearningModuleResponse> mockModules =
                List.of(new LearningModuleResponse("Book1", LocalDate.now(), 50),
                        new LearningModuleResponse("Book2", LocalDate.now().plusDays(1), 100));

        when(learningModuleRepository.findModuleStudyProgress(0, 100)).thenReturn(mockModules);

        DashboardStatsResponse response =
                learningProgressService.getDashboardStats("Book1", LocalDate.now());

        assertNotNull(response);
        assertEquals(1, response.getTotalModules());
        assertEquals(1, response.getCompletedModules());
        assertEquals(0, response.getActiveModules());
    }

    @Test
    void testGetAllModules() {
        List<LearningModuleResponse> mockModules =
                List.of(new LearningModuleResponse("Book1", LocalDate.now(), 50));
        when(learningModuleRepository.findModuleStudyProgress(0, 100)).thenReturn(mockModules);

        List<LearningModuleResponse> result = learningProgressService.getAllModules();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Book1", result.get(0).getBookName());
    }

    @Test
    void testGetDueModules() {
        List<LearningModuleResponse> mockModules =
                List.of(new LearningModuleResponse("Book1", LocalDate.now(), 50),
                        new LearningModuleResponse("Book2", LocalDate.now().plusDays(5), 100));

        when(learningModuleRepository.findModuleStudyProgress(0, 100)).thenReturn(mockModules);

        List<LearningModuleResponse> result = learningProgressService.getDueModules(7);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetCompletedModules() {
        List<LearningModuleResponse> mockModules =
                List.of(new LearningModuleResponse("Book1", LocalDate.now(), 100),
                        new LearningModuleResponse("Book2", LocalDate.now(), 0));

        when(learningModuleRepository.findModuleStudyProgress(0, 100)).thenReturn(mockModules);

        List<LearningModuleResponse> result = learningProgressService.getCompletedModules();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Book1", result.get(0).getBookName());
    }

    @Test
    void testGetUniqueBooks() {
        List<LearningModuleResponse> mockModules =
                List.of(new LearningModuleResponse("Book1", LocalDate.now(), 50),
                        new LearningModuleResponse("Book2", LocalDate.now(), 100));

        when(learningModuleRepository.findModuleStudyProgress(0, 100)).thenReturn(mockModules);

        List<String> result = learningProgressService.getUniqueBooks();

        assertNotNull(result);
        assertTrue(result.contains("All"));
        assertTrue(result.contains("Book1"));
        assertTrue(result.contains("Book2"));
    }

    @Test
    void testGetBookStats() {
        List<LearningModuleResponse> mockModules =
                List.of(new LearningModuleResponse("Book1", LocalDate.now(), 50),
                        new LearningModuleResponse("Book1", LocalDate.now(), 100));

        when(bookRepository.findByName("Book1")).thenReturn(Optional.of(new Book("Book1")));
        when(learningModuleRepository.findModuleStudyProgress(0, 100)).thenReturn(mockModules);

        BookStatsResponse response = learningProgressService.getBookStats("Book1");

        assertNotNull(response);
        assertEquals("Book1", response.getBookName());
        assertEquals(2, response.getTotalModules());
        assertEquals(1, response.getCompletedModules());
    }

    @Test
    void testExportData() {
        Map<String, String> result = learningProgressService.exportData();

        assertNotNull(result);
        assertEquals("Data exported successfully", result.get("message"));
        assertTrue(result.get("filePath").contains("learning_progress_export_"));
    }

    @Test
    void testGetBookStatsThrowsExceptionWhenBookNotFound() {
        when(bookRepository.findByName("NonExistentBook")).thenReturn(Optional.empty());

        assertThrows(SpacedLearningException.class,
                () -> learningProgressService.getBookStats("NonExistentBook"));
    }
}
