//package com.spacedlearning.service.impl.repetition;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.ArgumentMatchers.isA;
//import static org.mockito.Mockito.atLeast;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.spacedlearning.entity.BaseEntity;
//import com.spacedlearning.entity.Book;
//import com.spacedlearning.entity.Module;
//import com.spacedlearning.entity.ModuleProgress;
//import com.spacedlearning.entity.Repetition;
//import com.spacedlearning.entity.enums.BookStatus;
//import com.spacedlearning.entity.enums.CycleStudied;
//import com.spacedlearning.entity.enums.DifficultyLevel;
//import com.spacedlearning.entity.enums.RepetitionOrder;
//import com.spacedlearning.entity.enums.RepetitionStatus;
//import com.spacedlearning.repository.ModuleProgressRepository;
//import com.spacedlearning.repository.RepetitionRepository;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.aot.DisabledInAotMode;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ContextConfiguration(classes = {RepetitionScheduleManager.class})
//@ExtendWith(SpringExtension.class)
//@DisabledInAotMode
//class RepetitionScheduleManagerDiffblueTest {
//    @MockBean
//    private ModuleProgressRepository moduleProgressRepository;
//
//    @MockBean
//    private RepetitionRepository repetitionRepository;
//
//    @Autowired
//    private RepetitionScheduleManager repetitionScheduleManager;
//
//    /**
//     * Test {@link RepetitionScheduleManager#initializeFirstLearningDate(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#initializeFirstLearningDate(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test initializeFirstLearningDate(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testInitializeFirstLearningDate() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.initializeFirstLearningDate(progress);
//
//        // Assert that nothing has changed
//        assertEquals("1970-01-01", progress.getFirstLearningDate().toString());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#initializeFirstLearningDate(ModuleProgress)}.
//     * <ul>
//     *   <li>Then calls {@link CrudRepository#save(Object)}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#initializeFirstLearningDate(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test initializeFirstLearningDate(ModuleProgress); then calls save(Object)")
//    @Tag("MaintainedByDiffblue")
//    void testInitializeFirstLearningDate_thenCallsSave() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(Integer.valueOf(1));
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(Integer.valueOf(3));
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(Integer.valueOf(1));
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(Integer.valueOf(3));
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule2);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(null);
//
//        // Act
//        repetitionScheduleManager.initializeFirstLearningDate(progress);
//
//        // Assert
//        verify(moduleProgressRepository).save(isA(ModuleProgress.class));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(Integer.valueOf(1));
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(Integer.valueOf(3));
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress2() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(Integer.valueOf(1));
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(Integer.valueOf(1));
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(Integer.valueOf(3));
//
//        Book book2 = new Book();
//        book2.setBookNo(Integer.valueOf(1));
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(Integer.valueOf(1));
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(Integer.valueOf(3));
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.SECOND_REVIEW);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        LocalDate ofResult = LocalDate.of(1970, 1, 1);
//        when(progress.getFirstLearningDate()).thenReturn(ofResult);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2, atLeast(1)).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress, atLeast(1)).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress, atLeast(1)).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(Mockito.<LocalDate>any());
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//        assertEquals("1970-01-02", actualCreateRepetitionsForProgressResult.get(3).getReviewDate().toString());
//        assertEquals("1970-01-02", actualCreateRepetitionsForProgressResult.get(4).getReviewDate().toString());
//        assertSame(ofResult, actualCreateRepetitionsForProgressResult.get(2).getReviewDate());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress3() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_TIME);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        when(progress.getFirstLearningDate()).thenReturn(null);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2, atLeast(1)).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress, atLeast(1)).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress, atLeast(1)).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <ul>
//     *   <li>Given {@code FIRST_REVIEW}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress); given 'FIRST_REVIEW'")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress_givenFirstReview() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_REVIEW);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        LocalDate ofResult = LocalDate.of(1970, 1, 1);
//        when(progress.getFirstLearningDate()).thenReturn(ofResult);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2, atLeast(1)).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress, atLeast(1)).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress, atLeast(1)).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//        assertSame(ofResult, actualCreateRepetitionsForProgressResult.get(3).getReviewDate());
//        assertSame(ofResult, actualCreateRepetitionsForProgressResult.get(4).getReviewDate());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <ul>
//     *   <li>Given {@link Module} {@link Module#getWordCount()} return zero.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress); given Module getWordCount() return zero")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress_givenModuleGetWordCountReturnZero() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(0);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_TIME);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        LocalDate ofResult = LocalDate.of(1970, 1, 1);
//        when(progress.getFirstLearningDate()).thenReturn(ofResult);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2, atLeast(1)).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress, atLeast(1)).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress, atLeast(1)).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//        assertSame(ofResult, actualCreateRepetitionsForProgressResult.get(3).getReviewDate());
//        assertSame(ofResult, actualCreateRepetitionsForProgressResult.get(4).getReviewDate());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <ul>
//     *   <li>Then return first ReviewDate toString is {@code 1970-01-02}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress); then return first ReviewDate toString is '1970-01-02'")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress_thenReturnFirstReviewDateToStringIs19700102() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(5);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(Mockito.<LocalDate>any());
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//        Repetition getResult = actualCreateRepetitionsForProgressResult.get(0);
//        assertEquals("1970-01-02", getResult.getReviewDate().toString());
//        Repetition getResult2 = actualCreateRepetitionsForProgressResult.get(1);
//        assertEquals("1970-01-02", getResult2.getReviewDate().toString());
//        assertSame(progress, getResult.getModuleProgress());
//        assertSame(progress, getResult2.getModuleProgress());
//        assertSame(progress, actualCreateRepetitionsForProgressResult.get(2).getModuleProgress());
//        assertSame(progress, actualCreateRepetitionsForProgressResult.get(3).getModuleProgress());
//        assertSame(progress, actualCreateRepetitionsForProgressResult.get(4).getModuleProgress());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <ul>
//     *   <li>Then return first ReviewDate toString is {@code 1970-01-03}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress); then return first ReviewDate toString is '1970-01-03'")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress_thenReturnFirstReviewDateToStringIs19700103() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_TIME);
//        when(progress.getPercentComplete()).thenReturn(null);
//        when(progress.getFirstLearningDate()).thenReturn(LocalDate.of(1970, 1, 1));
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2, atLeast(1)).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress, atLeast(1)).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress, atLeast(1)).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(Mockito.<LocalDate>any());
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//        assertEquals("1970-01-03", actualCreateRepetitionsForProgressResult.get(0).getReviewDate().toString());
//        assertEquals("1970-01-05", actualCreateRepetitionsForProgressResult.get(1).getReviewDate().toString());
//        assertEquals("1970-01-09", actualCreateRepetitionsForProgressResult.get(2).getReviewDate().toString());
//        assertEquals("1970-01-14", actualCreateRepetitionsForProgressResult.get(3).getReviewDate().toString());
//        assertEquals("1970-01-20", actualCreateRepetitionsForProgressResult.get(4).getReviewDate().toString());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}.
//     * <ul>
//     *   <li>Then return third ReviewDate toString is {@code 1970-01-02}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#createRepetitionsForProgress(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test createRepetitionsForProgress(ModuleProgress); then return third ReviewDate toString is '1970-01-02'")
//    @Tag("MaintainedByDiffblue")
//    void testCreateRepetitionsForProgress_thenReturnThirdReviewDateToStringIs19700102() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.THIRD_REVIEW);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        when(progress.getFirstLearningDate()).thenReturn(LocalDate.of(1970, 1, 1));
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        List<Repetition> actualCreateRepetitionsForProgressResult = repetitionScheduleManager
//                .createRepetitionsForProgress(progress);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2, atLeast(1)).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress, atLeast(1)).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress, atLeast(1)).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(Mockito.<LocalDate>any());
//        assertEquals(5, actualCreateRepetitionsForProgressResult.size());
//        assertEquals("1970-01-02", actualCreateRepetitionsForProgressResult.get(2).getReviewDate().toString());
//        assertEquals("1970-01-02", actualCreateRepetitionsForProgressResult.get(3).getReviewDate().toString());
//        assertEquals("1970-01-02", actualCreateRepetitionsForProgressResult.get(4).getReviewDate().toString());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}
//     */
//    @Test
//    @DisplayName("Test rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)")
//    @Tag("MaintainedByDiffblue")
//    void testRescheduleFutureRepetitions() {
//        // Arrange
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.rescheduleFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION,
//                LocalDate.of(1970, 1, 1));
//
//        // Assert
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}
//     */
//    @Test
//    @DisplayName("Test rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)")
//    @Tag("MaintainedByDiffblue")
//    void testRescheduleFutureRepetitions2() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(-1);
//        book.setCategory("No future repetitions to reschedule for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("No future repetitions to reschedule for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(-1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("No future repetitions to reschedule for progress ID: {}");
//        book2.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.DRAFT);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Mr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(-1);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_REVIEW);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition2 = new Repetition();
//        repetition2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setId(UUID.randomUUID());
//        repetition2.setModuleProgress(moduleProgress2);
//        repetition2.setRepetitionOrder(RepetitionOrder.SECOND_REPETITION);
//        repetition2.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition2.setStatus(RepetitionStatus.COMPLETED);
//        repetition2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition2);
//        repetitionList.add(repetition);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.rescheduleFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION,
//                LocalDate.of(1970, 1, 1));
//
//        // Assert
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).saveAll(isA(Iterable.class));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}.
//     * <ul>
//     *   <li>Given {@link Book#Book()} BookNo is minus one.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}
//     */
//    @Test
//    @DisplayName("Test rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate); given Book() BookNo is minus one")
//    @Tag("MaintainedByDiffblue")
//    void testRescheduleFutureRepetitions_givenBookBookNoIsMinusOne() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(-1);
//        book.setCategory("No future repetitions to reschedule for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("No future repetitions to reschedule for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(-1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule2);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.rescheduleFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION,
//                LocalDate.of(1970, 1, 1));
//
//        // Assert
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}.
//     * <ul>
//     *   <li>Then calls {@link BaseEntity#setCreatedAt(LocalDateTime)}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate)}
//     */
//    @Test
//    @DisplayName("Test rescheduleFutureRepetitions(ModuleProgress, RepetitionOrder, LocalDate); then calls setCreatedAt(LocalDateTime)")
//    @Tag("MaintainedByDiffblue")
//    void testRescheduleFutureRepetitions_thenCallsSetCreatedAt() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(-1);
//        book.setCategory("No future repetitions to reschedule for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("No future repetitions to reschedule for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(-1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("No future repetitions to reschedule for progress ID: {}");
//        book2.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.DRAFT);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Mr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(-1);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_REVIEW);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        Repetition repetition2 = mock(Repetition.class);
//        when(repetition2.getRepetitionOrder()).thenReturn(RepetitionOrder.FIRST_REPETITION);
//        doNothing().when(repetition2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition2).setId(Mockito.<UUID>any());
//        doNothing().when(repetition2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition2).setModuleProgress(Mockito.<ModuleProgress>any());
//        doNothing().when(repetition2).setRepetitionOrder(Mockito.<RepetitionOrder>any());
//        doNothing().when(repetition2).setReviewDate(Mockito.<LocalDate>any());
//        doNothing().when(repetition2).setStatus(Mockito.<RepetitionStatus>any());
//        repetition2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setId(UUID.randomUUID());
//        repetition2.setModuleProgress(moduleProgress2);
//        repetition2.setRepetitionOrder(RepetitionOrder.SECOND_REPETITION);
//        repetition2.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition2.setStatus(RepetitionStatus.COMPLETED);
//        repetition2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition2);
//        repetitionList.add(repetition);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.rescheduleFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION,
//                LocalDate.of(1970, 1, 1));
//
//        // Assert
//        verify(repetition2).setCreatedAt(isA(LocalDateTime.class));
//        verify(repetition2).setDeletedAt(isA(LocalDateTime.class));
//        verify(repetition2).setId(isA(UUID.class));
//        verify(repetition2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(repetition2).getRepetitionOrder();
//        verify(repetition2).setModuleProgress(isA(ModuleProgress.class));
//        verify(repetition2).setRepetitionOrder(eq(RepetitionOrder.SECOND_REPETITION));
//        verify(repetition2).setReviewDate(isA(LocalDate.class));
//        verify(repetition2).setStatus(eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int)")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        LocalDate firstLearningDate = LocalDate.of(1970, 1, 1);
//        progress.setFirstLearningDate(firstLearningDate);
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        LocalDate actualCalculateReviewDateResult = repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals("1970-01-01", actualCalculateReviewDateResult.toString());
//        assertSame(firstLearningDate, actualCalculateReviewDateResult);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <ul>
//     *   <li>Given {@code FIRST_REVIEW}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int); given 'FIRST_REVIEW'")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate_givenFirstReview() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_REVIEW);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        LocalDate ofResult = LocalDate.of(1970, 1, 1);
//        when(progress.getFirstLearningDate()).thenReturn(ofResult);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        LocalDate actualCalculateReviewDateResult = repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals("1970-01-01", actualCalculateReviewDateResult.toString());
//        assertSame(ofResult, actualCalculateReviewDateResult);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <ul>
//     *   <li>Given {@link Module} {@link Module#getWordCount()} return zero.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int); given Module getWordCount() return zero")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate_givenModuleGetWordCountReturnZero() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(0);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_TIME);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        LocalDate ofResult = LocalDate.of(1970, 1, 1);
//        when(progress.getFirstLearningDate()).thenReturn(ofResult);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        LocalDate actualCalculateReviewDateResult = repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals("1970-01-01", actualCalculateReviewDateResult.toString());
//        assertSame(ofResult, actualCalculateReviewDateResult);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <ul>
//     *   <li>Given {@code SECOND_REVIEW}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int); given 'SECOND_REVIEW'")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate_givenSecondReview() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.SECOND_REVIEW);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        LocalDate ofResult = LocalDate.of(1970, 1, 1);
//        when(progress.getFirstLearningDate()).thenReturn(ofResult);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        LocalDate actualCalculateReviewDateResult = repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals("1970-01-01", actualCalculateReviewDateResult.toString());
//        assertSame(ofResult, actualCalculateReviewDateResult);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <ul>
//     *   <li>Given {@code THIRD_REVIEW}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int); given 'THIRD_REVIEW'")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate_givenThirdReview() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.THIRD_REVIEW);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        LocalDate ofResult = LocalDate.of(1970, 1, 1);
//        when(progress.getFirstLearningDate()).thenReturn(ofResult);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        LocalDate actualCalculateReviewDateResult = repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals("1970-01-01", actualCalculateReviewDateResult.toString());
//        assertSame(ofResult, actualCalculateReviewDateResult);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <ul>
//     *   <li>Then return toString is {@code 1970-01-02}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int); then return toString is '1970-01-02'")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate_thenReturnToStringIs19700102() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(4);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        LocalDate actualCalculateReviewDateResult = repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(Mockito.<LocalDate>any());
//        assertEquals("1970-01-02", actualCalculateReviewDateResult.toString());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <ul>
//     *   <li>Then return toString is {@code 1970-01-05}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int); then return toString is '1970-01-05'")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate_thenReturnToStringIs19700105() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_TIME);
//        when(progress.getPercentComplete()).thenReturn(null);
//        when(progress.getFirstLearningDate()).thenReturn(LocalDate.of(1970, 1, 1));
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        LocalDate actualCalculateReviewDateResult = repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//        assertEquals("1970-01-05", actualCalculateReviewDateResult.toString());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}.
//     * <ul>
//     *   <li>When {@link ModuleProgress} {@link ModuleProgress#getFirstLearningDate()} return {@code null}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#calculateReviewDate(ModuleProgress, int)}
//     */
//    @Test
//    @DisplayName("Test calculateReviewDate(ModuleProgress, int); when ModuleProgress getFirstLearningDate() return 'null'")
//    @Tag("MaintainedByDiffblue")
//    void testCalculateReviewDate_whenModuleProgressGetFirstLearningDateReturnNull() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//        Module resultModule2 = mock(Module.class);
//        when(resultModule2.getWordCount()).thenReturn(3);
//        doNothing().when(resultModule2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setId(Mockito.<UUID>any());
//        doNothing().when(resultModule2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(resultModule2).setBook(Mockito.<Book>any());
//        doNothing().when(resultModule2).setModuleNo(Mockito.<Integer>any());
//        doNothing().when(resultModule2).setProgress(Mockito.<List<ModuleProgress>>any());
//        doNothing().when(resultModule2).setTitle(Mockito.<String>any());
//        doNothing().when(resultModule2).setWordCount(Mockito.<Integer>any());
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule2);
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_TIME);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        when(progress.getFirstLearningDate()).thenReturn(null);
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.calculateReviewDate(progress, 1);
//
//        // Assert
//        verify(resultModule2).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(resultModule2).setId(isA(UUID.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(resultModule2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(resultModule2).getWordCount();
//        verify(resultModule2).setBook(isA(Book.class));
//        verify(resultModule2).setModuleNo(eq(1));
//        verify(resultModule2).setProgress(isA(List.class));
//        verify(resultModule2).setTitle(eq("Dr"));
//        verify(resultModule2).setWordCount(eq(3));
//        verify(progress).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress).getPercentComplete();
//        verify(progress).setCyclesStudied(eq(CycleStudied.FIRST_TIME));
//        verify(progress).setFirstLearningDate(isA(LocalDate.class));
//        verify(progress).setModule(isA(Module.class));
//        verify(progress).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateNextStudyDate(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateNextStudyDate(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test updateNextStudyDate(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateNextStudyDate() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress2);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateNextStudyDate(progress);
//
//        // Assert
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(moduleProgressRepository).save(isA(ModuleProgress.class));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateNextStudyDate(ModuleProgress)}.
//     * <ul>
//     *   <li>Given {@link ModuleProgressRepository}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateNextStudyDate(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test updateNextStudyDate(ModuleProgress); given ModuleProgressRepository")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateNextStudyDate_givenModuleProgressRepository() {
//        // Arrange
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateNextStudyDate(progress);
//
//        // Assert
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateNextStudyDate(ModuleProgress)}.
//     * <ul>
//     *   <li>Then calls {@link BaseEntity#setCreatedAt(LocalDateTime)}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateNextStudyDate(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test updateNextStudyDate(ModuleProgress); then calls setCreatedAt(LocalDateTime)")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateNextStudyDate_thenCallsSetCreatedAt() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        Repetition repetition = mock(Repetition.class);
//        when(repetition.getReviewDate()).thenReturn(LocalDate.of(1970, 1, 1));
//        doNothing().when(repetition).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition).setId(Mockito.<UUID>any());
//        doNothing().when(repetition).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition).setModuleProgress(Mockito.<ModuleProgress>any());
//        doNothing().when(repetition).setRepetitionOrder(Mockito.<RepetitionOrder>any());
//        doNothing().when(repetition).setReviewDate(Mockito.<LocalDate>any());
//        doNothing().when(repetition).setStatus(Mockito.<RepetitionStatus>any());
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress2);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateNextStudyDate(progress);
//
//        // Assert
//        verify(repetition).setCreatedAt(isA(LocalDateTime.class));
//        verify(repetition).setDeletedAt(isA(LocalDateTime.class));
//        verify(repetition).setId(isA(UUID.class));
//        verify(repetition).setUpdatedAt(isA(LocalDateTime.class));
//        verify(repetition).getReviewDate();
//        verify(repetition).setModuleProgress(isA(ModuleProgress.class));
//        verify(repetition).setRepetitionOrder(eq(RepetitionOrder.FIRST_REPETITION));
//        verify(repetition).setReviewDate(isA(LocalDate.class));
//        verify(repetition).setStatus(eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(moduleProgressRepository).save(isA(ModuleProgress.class));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}
//     */
//    @Test
//    @DisplayName("Test updateFutureRepetitions(ModuleProgress, RepetitionOrder)")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateFutureRepetitions() {
//        // Arrange
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION);
//
//        // Assert
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}
//     */
//    @Test
//    @DisplayName("Test updateFutureRepetitions(ModuleProgress, RepetitionOrder)")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateFutureRepetitions2() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(-1);
//        book.setCategory("No future repetitions to update for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("No future repetitions to update for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(-1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("No future repetitions to update for progress ID: {}");
//        book2.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.DRAFT);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Mr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(-1);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_REVIEW);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition2 = new Repetition();
//        repetition2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setId(UUID.randomUUID());
//        repetition2.setModuleProgress(moduleProgress2);
//        repetition2.setRepetitionOrder(RepetitionOrder.SECOND_REPETITION);
//        repetition2.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition2.setStatus(RepetitionStatus.COMPLETED);
//        repetition2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition2);
//        repetitionList.add(repetition);
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION);
//
//        // Assert
//        verify(repetitionRepository).countReviewDateExisted(isA(LocalDate.class));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).saveAll(isA(Iterable.class));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}
//     */
//    @Test
//    @DisplayName("Test updateFutureRepetitions(ModuleProgress, RepetitionOrder)")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateFutureRepetitions3() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(-1);
//        book.setCategory("No future repetitions to update for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("No future repetitions to update for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(-1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("No future repetitions to update for progress ID: {}");
//        book2.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.DRAFT);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Mr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(-1);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_REVIEW);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition2 = new Repetition();
//        repetition2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setId(UUID.randomUUID());
//        repetition2.setModuleProgress(moduleProgress2);
//        repetition2.setRepetitionOrder(RepetitionOrder.SECOND_REPETITION);
//        repetition2.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition2.setStatus(RepetitionStatus.COMPLETED);
//        repetition2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition2);
//        repetitionList.add(repetition);
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(4);
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION);
//
//        // Assert
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(Mockito.<LocalDate>any());
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).saveAll(isA(Iterable.class));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}.
//     * <ul>
//     *   <li>Given {@link Book#Book()} BookNo is minus one.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}
//     */
//    @Test
//    @DisplayName("Test updateFutureRepetitions(ModuleProgress, RepetitionOrder); given Book() BookNo is minus one")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateFutureRepetitions_givenBookBookNoIsMinusOne() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(-1);
//        book.setCategory("No future repetitions to update for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("No future repetitions to update for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(-1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule2);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION);
//
//        // Assert
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}.
//     * <ul>
//     *   <li>Then calls {@link BaseEntity#setCreatedAt(LocalDateTime)}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#updateFutureRepetitions(ModuleProgress, RepetitionOrder)}
//     */
//    @Test
//    @DisplayName("Test updateFutureRepetitions(ModuleProgress, RepetitionOrder); then calls setCreatedAt(LocalDateTime)")
//    @Tag("MaintainedByDiffblue")
//    void testUpdateFutureRepetitions_thenCallsSetCreatedAt() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(-1);
//        book.setCategory("No future repetitions to update for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("No future repetitions to update for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(-1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("No future repetitions to update for progress ID: {}");
//        book2.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.DRAFT);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Mr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(-1);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_REVIEW);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        Repetition repetition2 = mock(Repetition.class);
//        when(repetition2.getRepetitionOrder()).thenReturn(RepetitionOrder.FIRST_REPETITION);
//        doNothing().when(repetition2).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition2).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition2).setId(Mockito.<UUID>any());
//        doNothing().when(repetition2).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(repetition2).setModuleProgress(Mockito.<ModuleProgress>any());
//        doNothing().when(repetition2).setRepetitionOrder(Mockito.<RepetitionOrder>any());
//        doNothing().when(repetition2).setReviewDate(Mockito.<LocalDate>any());
//        doNothing().when(repetition2).setStatus(Mockito.<RepetitionStatus>any());
//        repetition2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setId(UUID.randomUUID());
//        repetition2.setModuleProgress(moduleProgress2);
//        repetition2.setRepetitionOrder(RepetitionOrder.SECOND_REPETITION);
//        repetition2.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition2.setStatus(RepetitionStatus.COMPLETED);
//        repetition2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition2);
//        repetitionList.add(repetition);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.updateFutureRepetitions(progress, RepetitionOrder.FIRST_REPETITION);
//
//        // Assert
//        verify(repetition2).setCreatedAt(isA(LocalDateTime.class));
//        verify(repetition2).setDeletedAt(isA(LocalDateTime.class));
//        verify(repetition2).setId(isA(UUID.class));
//        verify(repetition2).setUpdatedAt(isA(LocalDateTime.class));
//        verify(repetition2).getRepetitionOrder();
//        verify(repetition2).setModuleProgress(isA(ModuleProgress.class));
//        verify(repetition2).setRepetitionOrder(eq(RepetitionOrder.SECOND_REPETITION));
//        verify(repetition2).setReviewDate(isA(LocalDate.class));
//        verify(repetition2).setStatus(eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByRepetitionOrder(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(Mockito.<UUID>any()))
//                .thenReturn(new ArrayList<>());
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        Optional<LocalDate> ofResult = Optional.of(LocalDate.of(1970, 1, 1));
//        when(repetitionRepository.findLastCompletedRepetitionDate(Mockito.<UUID>any())).thenReturn(ofResult);
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(1L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule2);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdOrderByRepetitionOrder(isA(UUID.class));
//        verify(repetitionRepository).findLastCompletedRepetitionDate(isA(UUID.class));
//        verify(moduleProgressRepository, atLeast(1)).save(isA(ModuleProgress.class));
//        verify(repetitionRepository, atLeast(1)).saveAll(Mockito.<Iterable<Repetition>>any());
//        LocalDate firstLearningDate = progress.getFirstLearningDate();
//        assertEquals("1970-01-02", firstLearningDate.toString());
//        assertEquals(CycleStudied.FIRST_REVIEW, progress.getCyclesStudied());
//        assertSame(firstLearningDate, progress.getNextStudyDate());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied2() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Updated cycle studied to {} for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Updated cycle studied to {} for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition);
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(Mockito.<UUID>any()))
//                .thenReturn(repetitionList);
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        Optional<LocalDate> ofResult = Optional.of(LocalDate.of(1970, 1, 1));
//        when(repetitionRepository.findLastCompletedRepetitionDate(Mockito.<UUID>any())).thenReturn(ofResult);
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(1L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress2);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdOrderByRepetitionOrder(isA(UUID.class));
//        verify(repetitionRepository).findLastCompletedRepetitionDate(isA(UUID.class));
//        verify(moduleProgressRepository, atLeast(1)).save(isA(ModuleProgress.class));
//        verify(repetitionRepository, atLeast(1)).saveAll(Mockito.<Iterable<Repetition>>any());
//        LocalDate firstLearningDate = progress.getFirstLearningDate();
//        assertEquals("1970-01-02", firstLearningDate.toString());
//        assertEquals(CycleStudied.FIRST_REVIEW, progress.getCyclesStudied());
//        assertSame(firstLearningDate, progress.getNextStudyDate());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress)")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied3() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(Mockito.<UUID>any()))
//                .thenReturn(new ArrayList<>());
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        Optional<LocalDate> emptyResult = Optional.empty();
//        when(repetitionRepository.findLastCompletedRepetitionDate(Mockito.<UUID>any())).thenReturn(emptyResult);
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(1L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule2);
//        LocalDate nextStudyDate = LocalDate.of(1970, 1, 1);
//        progress.setNextStudyDate(nextStudyDate);
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdOrderByRepetitionOrder(isA(UUID.class));
//        verify(repetitionRepository).findLastCompletedRepetitionDate(isA(UUID.class));
//        verify(moduleProgressRepository).save(isA(ModuleProgress.class));
//        verify(repetitionRepository, atLeast(1)).saveAll(Mockito.<Iterable<Repetition>>any());
//        assertEquals("1970-01-01", progress.getFirstLearningDate().toString());
//        LocalDate nextStudyDate2 = progress.getNextStudyDate();
//        assertEquals("1970-01-01", nextStudyDate2.toString());
//        assertEquals(CycleStudied.FIRST_REVIEW, progress.getCyclesStudied());
//        assertSame(nextStudyDate, nextStudyDate2);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <ul>
//     *   <li>Given {@link Book#Book()} BookNo is two.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress); given Book() BookNo is two")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied_givenBookBookNoIsTwo() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Updated cycle studied to {} for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Updated cycle studied to {} for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        repetition.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Book book2 = new Book();
//        book2.setBookNo(2);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("Updated cycle studied to {} for progress ID: {}");
//        book2.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.DRAFT);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(2);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Mr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(1);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_REVIEW);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition2 = new Repetition();
//        repetition2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition2.setId(UUID.randomUUID());
//        repetition2.setModuleProgress(moduleProgress2);
//        repetition2.setRepetitionOrder(RepetitionOrder.SECOND_REPETITION);
//        repetition2.setReviewDate(LocalDate.of(1970, 1, 1));
//        repetition2.setStatus(RepetitionStatus.COMPLETED);
//        repetition2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition2);
//        repetitionList.add(repetition);
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(Mockito.<UUID>any()))
//                .thenReturn(repetitionList);
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        Optional<LocalDate> ofResult = Optional.of(LocalDate.of(1970, 1, 1));
//        when(repetitionRepository.findLastCompletedRepetitionDate(Mockito.<UUID>any())).thenReturn(ofResult);
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(1L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress moduleProgress3 = new ModuleProgress();
//        moduleProgress3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress3.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress3.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress3.setId(UUID.randomUUID());
//        moduleProgress3.setModule(resultModule3);
//        moduleProgress3.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress3.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress3.setRepetitions(new ArrayList<>());
//        moduleProgress3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress3);
//
//        Book book4 = new Book();
//        book4.setBookNo(1);
//        book4.setCategory("Category");
//        book4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book4.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book4.setDescription("The characteristics of someone or something");
//        book4.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book4.setId(UUID.randomUUID());
//        book4.setModules(new ArrayList<>());
//        book4.setName("Name");
//        book4.setStatus(BookStatus.PUBLISHED);
//        book4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book4.setUsers(new HashSet<>());
//
//        Module resultModule4 = new Module();
//        resultModule4.setBook(book4);
//        resultModule4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule4.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule4.setId(UUID.randomUUID());
//        resultModule4.setModuleNo(1);
//        resultModule4.setProgress(new ArrayList<>());
//        resultModule4.setTitle("Dr");
//        resultModule4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule4.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule4);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdOrderByRepetitionOrder(isA(UUID.class));
//        verify(repetitionRepository).findLastCompletedRepetitionDate(isA(UUID.class));
//        verify(moduleProgressRepository, atLeast(1)).save(isA(ModuleProgress.class));
//        verify(repetitionRepository, atLeast(1)).saveAll(Mockito.<Iterable<Repetition>>any());
//        LocalDate firstLearningDate = progress.getFirstLearningDate();
//        assertEquals("1970-01-02", firstLearningDate.toString());
//        assertEquals(CycleStudied.FIRST_REVIEW, progress.getCyclesStudied());
//        assertSame(firstLearningDate, progress.getNextStudyDate());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <ul>
//     *   <li>Then calls {@link BaseEntity#getId()}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress); then calls getId()")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied_thenCallsGetId() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(Mockito.<UUID>any()))
//                .thenReturn(new ArrayList<>());
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        Optional<LocalDate> ofResult = Optional.of(LocalDate.of(1970, 1, 1));
//        when(repetitionRepository.findLastCompletedRepetitionDate(Mockito.<UUID>any())).thenReturn(ofResult);
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(1L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//        ModuleProgress progress = mock(ModuleProgress.class);
//        when(progress.getModule()).thenReturn(resultModule3);
//        when(progress.getPercentComplete()).thenReturn(new BigDecimal("2.3"));
//        when(progress.getFirstLearningDate()).thenReturn(LocalDate.of(1970, 1, 1));
//        when(progress.getCyclesStudied()).thenReturn(CycleStudied.FIRST_TIME);
//        when(progress.getId()).thenReturn(UUID.randomUUID());
//        doNothing().when(progress).setCreatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setDeletedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setId(Mockito.<UUID>any());
//        doNothing().when(progress).setUpdatedAt(Mockito.<LocalDateTime>any());
//        doNothing().when(progress).setCyclesStudied(Mockito.<CycleStudied>any());
//        doNothing().when(progress).setFirstLearningDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setModule(Mockito.<Module>any());
//        doNothing().when(progress).setNextStudyDate(Mockito.<LocalDate>any());
//        doNothing().when(progress).setPercentComplete(Mockito.<BigDecimal>any());
//        doNothing().when(progress).setRepetitions(Mockito.<List<Repetition>>any());
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule2);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert
//        verify(progress, atLeast(1)).getId();
//        verify(progress).setCreatedAt(isA(LocalDateTime.class));
//        verify(progress).setDeletedAt(isA(LocalDateTime.class));
//        verify(progress).setId(isA(UUID.class));
//        verify(progress).setUpdatedAt(isA(LocalDateTime.class));
//        verify(progress, atLeast(1)).getCyclesStudied();
//        verify(progress, atLeast(1)).getFirstLearningDate();
//        verify(progress, atLeast(1)).getModule();
//        verify(progress, atLeast(1)).getPercentComplete();
//        verify(progress, atLeast(1)).setCyclesStudied(Mockito.<CycleStudied>any());
//        verify(progress, atLeast(1)).setFirstLearningDate(Mockito.<LocalDate>any());
//        verify(progress).setModule(isA(Module.class));
//        verify(progress, atLeast(1)).setNextStudyDate(isA(LocalDate.class));
//        verify(progress).setPercentComplete(isA(BigDecimal.class));
//        verify(progress).setRepetitions(isA(List.class));
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdOrderByRepetitionOrder(isA(UUID.class));
//        verify(repetitionRepository).findLastCompletedRepetitionDate(isA(UUID.class));
//        verify(moduleProgressRepository, atLeast(1)).save(isA(ModuleProgress.class));
//        verify(repetitionRepository, atLeast(1)).saveAll(Mockito.<Iterable<Repetition>>any());
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <ul>
//     *   <li>Then {@link ModuleProgress#ModuleProgress()} CyclesStudied is {@code FIRST_TIME}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress); then ModuleProgress() CyclesStudied is 'FIRST_TIME'")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied_thenModuleProgressCyclesStudiedIsFirstTime() {
//        // Arrange
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(3L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule);
//        LocalDate nextStudyDate = LocalDate.of(1970, 1, 1);
//        progress.setNextStudyDate(nextStudyDate);
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert that nothing has changed
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        assertEquals("1970-01-01", progress.getFirstLearningDate().toString());
//        LocalDate nextStudyDate2 = progress.getNextStudyDate();
//        assertEquals("1970-01-01", nextStudyDate2.toString());
//        assertEquals(CycleStudied.FIRST_TIME, progress.getCyclesStudied());
//        assertSame(nextStudyDate, nextStudyDate2);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <ul>
//     *   <li>Then {@link ModuleProgress#ModuleProgress()} NextStudyDate toString is {@code 1970-01-01}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress); then ModuleProgress() NextStudyDate toString is '1970-01-01'")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied_thenModuleProgressNextStudyDateToStringIs19700101() {
//        // Arrange
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Updated cycle studied to {} for progress ID: {}");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Updated cycle studied to {} for progress ID: {}");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        Repetition repetition = new Repetition();
//        repetition.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        repetition.setId(UUID.randomUUID());
//        repetition.setModuleProgress(moduleProgress);
//        repetition.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);
//        LocalDate reviewDate = LocalDate.of(1970, 1, 1);
//        repetition.setReviewDate(reviewDate);
//        repetition.setStatus(RepetitionStatus.NOT_STARTED);
//        repetition.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        ArrayList<Repetition> repetitionList = new ArrayList<>();
//        repetitionList.add(repetition);
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(3);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(repetitionList);
//        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(Mockito.<UUID>any()))
//                .thenReturn(new ArrayList<>());
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        Optional<LocalDate> ofResult = Optional.of(LocalDate.of(1970, 1, 1));
//        when(repetitionRepository.findLastCompletedRepetitionDate(Mockito.<UUID>any())).thenReturn(ofResult);
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(1L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress moduleProgress2 = new ModuleProgress();
//        moduleProgress2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress2.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setId(UUID.randomUUID());
//        moduleProgress2.setModule(resultModule2);
//        moduleProgress2.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress2.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress2.setRepetitions(new ArrayList<>());
//        moduleProgress2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress2);
//
//        Book book3 = new Book();
//        book3.setBookNo(1);
//        book3.setCategory("Category");
//        book3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setDescription("The characteristics of someone or something");
//        book3.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book3.setId(UUID.randomUUID());
//        book3.setModules(new ArrayList<>());
//        book3.setName("Name");
//        book3.setStatus(BookStatus.PUBLISHED);
//        book3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book3.setUsers(new HashSet<>());
//
//        Module resultModule3 = new Module();
//        resultModule3.setBook(book3);
//        resultModule3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setId(UUID.randomUUID());
//        resultModule3.setModuleNo(1);
//        resultModule3.setProgress(new ArrayList<>());
//        resultModule3.setTitle("Dr");
//        resultModule3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule3.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule3);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(isA(LocalDate.class));
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdOrderByRepetitionOrder(isA(UUID.class));
//        verify(repetitionRepository).findLastCompletedRepetitionDate(isA(UUID.class));
//        verify(moduleProgressRepository, atLeast(1)).save(isA(ModuleProgress.class));
//        verify(repetitionRepository, atLeast(1)).saveAll(Mockito.<Iterable<Repetition>>any());
//        LocalDate nextStudyDate = progress.getNextStudyDate();
//        assertEquals("1970-01-01", nextStudyDate.toString());
//        assertEquals("1970-01-02", progress.getFirstLearningDate().toString());
//        assertEquals(CycleStudied.FIRST_REVIEW, progress.getCyclesStudied());
//        assertSame(reviewDate, nextStudyDate);
//    }
//
//    /**
//     * Test {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}.
//     * <ul>
//     *   <li>Then {@link ModuleProgress#ModuleProgress()} NextStudyDate toString is {@code 1970-01-03}.</li>
//     * </ul>
//     * <p>
//     * Method under test: {@link RepetitionScheduleManager#checkAndUpdateCycleStudied(ModuleProgress)}
//     */
//    @Test
//    @DisplayName("Test checkAndUpdateCycleStudied(ModuleProgress); then ModuleProgress() NextStudyDate toString is '1970-01-03'")
//    @Tag("MaintainedByDiffblue")
//    void testCheckAndUpdateCycleStudied_thenModuleProgressNextStudyDateToStringIs19700103() {
//        // Arrange
//        when(repetitionRepository.countReviewDateExisted(Mockito.<LocalDate>any())).thenReturn(4);
//        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByReviewDate(Mockito.<UUID>any(),
//                Mockito.<RepetitionStatus>any())).thenReturn(new ArrayList<>());
//        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(Mockito.<UUID>any()))
//                .thenReturn(new ArrayList<>());
//        when(repetitionRepository.saveAll(Mockito.<Iterable<Repetition>>any())).thenReturn(new ArrayList<>());
//        Optional<LocalDate> ofResult = Optional.of(LocalDate.of(1970, 1, 1));
//        when(repetitionRepository.findLastCompletedRepetitionDate(Mockito.<UUID>any())).thenReturn(ofResult);
//        when(repetitionRepository.countByModuleProgressId(Mockito.<UUID>any())).thenReturn(1L);
//        when(repetitionRepository.countByModuleProgressIdAndStatus(Mockito.<UUID>any(), Mockito.<RepetitionStatus>any()))
//                .thenReturn(1L);
//
//        Book book = new Book();
//        book.setBookNo(1);
//        book.setCategory("Category");
//        book.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setDescription("The characteristics of someone or something");
//        book.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book.setId(UUID.randomUUID());
//        book.setModules(new ArrayList<>());
//        book.setName("Name");
//        book.setStatus(BookStatus.PUBLISHED);
//        book.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book.setUsers(new HashSet<>());
//
//        Module resultModule = new Module();
//        resultModule.setBook(book);
//        resultModule.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setId(UUID.randomUUID());
//        resultModule.setModuleNo(1);
//        resultModule.setProgress(new ArrayList<>());
//        resultModule.setTitle("Dr");
//        resultModule.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule.setWordCount(3);
//
//        ModuleProgress moduleProgress = new ModuleProgress();
//        moduleProgress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        moduleProgress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        moduleProgress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setId(UUID.randomUUID());
//        moduleProgress.setModule(resultModule);
//        moduleProgress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        moduleProgress.setPercentComplete(new BigDecimal("2.3"));
//        moduleProgress.setRepetitions(new ArrayList<>());
//        moduleProgress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        when(moduleProgressRepository.save(Mockito.<ModuleProgress>any())).thenReturn(moduleProgress);
//
//        Book book2 = new Book();
//        book2.setBookNo(1);
//        book2.setCategory("Category");
//        book2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setDescription("The characteristics of someone or something");
//        book2.setDifficultyLevel(DifficultyLevel.BEGINNER);
//        book2.setId(UUID.randomUUID());
//        book2.setModules(new ArrayList<>());
//        book2.setName("Name");
//        book2.setStatus(BookStatus.PUBLISHED);
//        book2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        book2.setUsers(new HashSet<>());
//
//        Module resultModule2 = new Module();
//        resultModule2.setBook(book2);
//        resultModule2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setId(UUID.randomUUID());
//        resultModule2.setModuleNo(1);
//        resultModule2.setProgress(new ArrayList<>());
//        resultModule2.setTitle("Dr");
//        resultModule2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        resultModule2.setWordCount(3);
//
//        ModuleProgress progress = new ModuleProgress();
//        progress.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
//        progress.setDeletedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//        progress.setFirstLearningDate(LocalDate.of(1970, 1, 1));
//        progress.setId(UUID.randomUUID());
//        progress.setModule(resultModule2);
//        progress.setNextStudyDate(LocalDate.of(1970, 1, 1));
//        progress.setPercentComplete(new BigDecimal("2.3"));
//        progress.setRepetitions(new ArrayList<>());
//        progress.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
//
//        // Act
//        repetitionScheduleManager.checkAndUpdateCycleStudied(progress);
//
//        // Assert
//        verify(repetitionRepository).countByModuleProgressId(isA(UUID.class));
//        verify(repetitionRepository).countByModuleProgressIdAndStatus(isA(UUID.class), eq(RepetitionStatus.COMPLETED));
//        verify(repetitionRepository, atLeast(1)).countReviewDateExisted(Mockito.<LocalDate>any());
//        verify(repetitionRepository).findByModuleProgressIdAndStatusOrderByReviewDate(isA(UUID.class),
//                eq(RepetitionStatus.NOT_STARTED));
//        verify(repetitionRepository).findByModuleProgressIdOrderByRepetitionOrder(isA(UUID.class));
//        verify(repetitionRepository).findLastCompletedRepetitionDate(isA(UUID.class));
//        verify(moduleProgressRepository, atLeast(1)).save(isA(ModuleProgress.class));
//        verify(repetitionRepository, atLeast(1)).saveAll(Mockito.<Iterable<Repetition>>any());
//        assertEquals("1970-01-02", progress.getFirstLearningDate().toString());
//        assertEquals("1970-01-03", progress.getNextStudyDate().toString());
//        assertEquals(CycleStudied.FIRST_REVIEW, progress.getCyclesStudied());
//    }
//}
