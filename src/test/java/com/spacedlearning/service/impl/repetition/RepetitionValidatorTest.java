package com.spacedlearning.service.impl.repetition;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;



@ExtendWith(MockitoExtension.class)
class RepetitionValidatorTest {

    @Mock
    private RepetitionRepository repetitionRepository;

    @Mock
    private ModuleProgressRepository progressRepository;

    @InjectMocks
    private RepetitionValidator validator;

    private UUID id;
    private ModuleProgress moduleProgress;
    private Repetition repetition;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        moduleProgress = new ModuleProgress();
        repetition = new Repetition();
    }

    @Test
    void findModuleProgress_WhenExists_ReturnsModuleProgress() {
        when(progressRepository.findById(id)).thenReturn(Optional.of(moduleProgress));

        ModuleProgress result = validator.findModuleProgress(id);

        assertEquals(moduleProgress, result);
    }

    @Test
    void findModuleProgress_WhenNotExists_ThrowsException() {
        when(progressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(SpacedLearningException.class, () -> validator.findModuleProgress(id));
    }

    @Test
    void findRepetition_WhenExists_ReturnsRepetition() {
        when(repetitionRepository.findById(id)).thenReturn(Optional.of(repetition));

        Repetition result = validator.findRepetition(id);

        assertEquals(repetition, result);
    }

    @Test
    void findRepetition_WhenNotExists_ThrowsException() {
        when(repetitionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(SpacedLearningException.class, () -> validator.findRepetition(id));
    }

    @Test
    void validateRepetitionDoesNotExist_WhenExists_ThrowsException() {
        when(repetitionRepository.existsByModuleProgressIdAndRepetitionOrder(id,
                RepetitionOrder.FIRST_REPETITION)).thenReturn(true);

        assertThrows(SpacedLearningException.class, () -> validator
                .validateRepetitionDoesNotExist(id, RepetitionOrder.FIRST_REPETITION));
    }

    @Test
    void validateRepetitionDoesNotExist_WhenNotExists_DoesNotThrow() {
        when(repetitionRepository.existsByModuleProgressIdAndRepetitionOrder(id,
                RepetitionOrder.FIRST_REPETITION)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateRepetitionDoesNotExist(id,
                RepetitionOrder.FIRST_REPETITION));
    }

    @Test
    void validateModuleProgressExists_WhenExists_DoesNotThrow() {
        when(progressRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateModuleProgressExists(id));
    }

    @Test
    void validateModuleProgressExists_WhenNotExists_ThrowsException() {
        when(progressRepository.existsById(id)).thenReturn(false);

        assertThrows(SpacedLearningException.class,
                () -> validator.validateModuleProgressExists(id));
    }
}
