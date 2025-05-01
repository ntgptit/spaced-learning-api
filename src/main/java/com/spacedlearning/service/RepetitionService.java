// File: src/main/java/com/spacedlearning/service/RepetitionService.java
package com.spacedlearning.service;

import com.spacedlearning.dto.repetition.*;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Repetition operations
 */
public interface RepetitionService {

    /**
     * Create a new repetition
     *
     * @param request Repetition creation request
     * @return Created repetition response
     */
    RepetitionResponse create(RepetitionCreateRequest request);

    /**
     * Create default repetition schedule for a module progress
     *
     * @param moduleProgressId Module progress ID
     * @return List of created repetition responses
     */
    List<RepetitionResponse> createDefaultSchedule(UUID moduleProgressId);

    /**
     * Delete a repetition
     *
     * @param id Repetition ID
     */
    void delete(UUID id);

    /**
     * Find all repetitions with pagination
     *
     * @param pageable Pagination information
     * @return Page of repetition responses
     */
    Page<RepetitionResponse> findAll(Pageable pageable);

    /**
     * Find repetition by ID
     *
     * @param id Repetition ID
     * @return Repetition response
     */
    RepetitionResponse findById(UUID id);

    /**
     * Find repetitions by module progress ID
     *
     * @param moduleProgressId Module progress ID
     * @return List of repetition responses
     */
    List<RepetitionResponse> findByModuleProgressId(UUID moduleProgressId);

    /**
     * Find repetition by module progress ID and order
     *
     * @param moduleProgressId Module progress ID
     * @param repetitionOrder  Repetition order
     * @return Repetition response
     */
    RepetitionResponse findByModuleProgressIdAndOrder(UUID moduleProgressId, RepetitionOrder repetitionOrder);

    /**
     * Find repetitions due for review
     *
     * @param userId     User ID
     * @param reviewDate Date to review on or before
     * @param status     Status to filter by
     * @param pageable   Pagination information
     * @return Page of repetition responses
     */
    Page<RepetitionResponse> findDueRepetitions(UUID userId, LocalDate reviewDate, RepetitionStatus status,
                                                Pageable pageable);

    /**
     * Update a repetition (deprecated, use updateCompletion or reschedule instead)
     *
     * @param id      Repetition ID
     * @param request Repetition update request
     * @return Updated repetition response
     */
    RepetitionResponse update(UUID id, RepetitionUpdateRequest request);

    /**
     * Update repetition completion status
     *
     * @param id      Repetition ID
     * @param request Repetition completion request
     * @return Updated repetition response
     */
    RepetitionResponse updateCompletion(UUID id, RepetitionCompletionRequest request);

    /**
     * Reschedule a repetition
     *
     * @param id      Repetition ID
     * @param request Repetition reschedule request
     * @return Rescheduled repetition response
     */
    RepetitionResponse reschedule(UUID id, RepetitionRescheduleRequest request);
}