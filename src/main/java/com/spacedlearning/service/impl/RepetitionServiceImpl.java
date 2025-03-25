// File: src/main/java/com/spacedlearning/service/impl/RepetitionServiceImpl.java
package com.spacedlearning.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.RepetitionMapper;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;
import com.spacedlearning.service.RepetitionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of RepetitionService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepetitionServiceImpl implements RepetitionService {

	// Spaced repetition intervals in days (Fibonacci sequence)
	private static final int[] REPETITION_INTERVALS = { 1, 2, 3, 5, 8 };
	private final RepetitionRepository repetitionRepository;
	private final ModuleProgressRepository progressRepository;

	private final RepetitionMapper repetitionMapper;

	@Override
	@Transactional
	public RepetitionResponse create(RepetitionCreateRequest request) {
		log.debug("Creating new repetition: {}", request);

		// Validate module progress existence
		final ModuleProgress progress = progressRepository.findById(request.getModuleProgressId()).orElseThrow(
				() -> SpacedLearningException.resourceNotFound("ModuleProgress", request.getModuleProgressId()));

		// Check if repetition already exists
		if (repetitionRepository.existsByModuleProgressIdAndRepetitionOrder(request.getModuleProgressId(),
				request.getRepetitionOrder())) {
			throw SpacedLearningException.resourceAlreadyExists("Repetition", "module_progress_id and repetition_order",
					request.getModuleProgressId() + ", " + request.getRepetitionOrder());
		}

		// Create and save repetition
		final Repetition repetition = repetitionMapper.toEntity(request, progress);
		final Repetition savedRepetition = repetitionRepository.save(repetition);

		log.info("Repetition created successfully with ID: {}", savedRepetition.getId());
		return repetitionMapper.toDto(savedRepetition);
	}

	@Override
	@Transactional
	public List<RepetitionResponse> createDefaultSchedule(UUID moduleProgressId) {
		log.debug("Creating default repetition schedule for module progress ID: {}", moduleProgressId);

		// Validate module progress existence
		final ModuleProgress progress = progressRepository.findById(moduleProgressId)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("ModuleProgress", moduleProgressId));

		// Check if schedule already exists
		final List<Repetition> existingRepetitions = repetitionRepository
				.findByModuleProgressIdOrderByRepetitionOrder(moduleProgressId);
		if (!existingRepetitions.isEmpty()) {
			log.info("Repetition schedule already exists for module progress ID: {}", moduleProgressId);
			return repetitionMapper.toDtoList(existingRepetitions);
		}

		// Get reference date (first learning date or today)
		LocalDate referenceDate = progress.getFirstLearningDate();
		if (referenceDate == null) {
			referenceDate = LocalDate.now();
			progress.setFirstLearningDate(referenceDate);
			progressRepository.save(progress);
		}

		final List<Repetition> repetitions = new ArrayList<>();
		final RepetitionOrder[] orders = RepetitionOrder.values();

		// Create repetitions for each order with appropriate intervals
		for (int i = 0; i < orders.length && i < REPETITION_INTERVALS.length; i++) {
			final RepetitionOrder order = orders[i];
			final int intervalDays = REPETITION_INTERVALS[i];

			final LocalDate reviewDate = referenceDate.plus(intervalDays, ChronoUnit.DAYS);

			final Repetition repetition = new Repetition();
			repetition.setModuleProgress(progress);
			repetition.setRepetitionOrder(order);
			repetition.setStatus(RepetitionStatus.NOT_STARTED);
			repetition.setReviewDate(reviewDate);

			repetitions.add(repetition);
		}

		final List<Repetition> savedRepetitions = repetitionRepository.saveAll(repetitions);
		log.info("Created default repetition schedule with {} repetitions for module progress ID: {}",
				savedRepetitions.size(), moduleProgressId);

		return repetitionMapper.toDtoList(savedRepetitions);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		log.debug("Deleting repetition with ID: {}", id);

		final Repetition repetition = repetitionRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));

		repetition.softDelete(); // Use soft delete
		repetitionRepository.save(repetition);

		log.info("Repetition soft deleted successfully with ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RepetitionResponse> findAll(Pageable pageable) {
		log.debug("Finding all repetitions with pagination: {}", pageable);
		return repetitionRepository.findAll(pageable).map(repetitionMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public RepetitionResponse findById(UUID id) {
		log.debug("Finding repetition by ID: {}", id);
		final Repetition repetition = repetitionRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));
		return repetitionMapper.toDto(repetition);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RepetitionResponse> findByModuleProgressId(UUID moduleProgressId) {
		log.debug("Finding repetitions by module progress ID: {}", moduleProgressId);

		// Verify module progress exists
		if (!progressRepository.existsById(moduleProgressId)) {
			throw SpacedLearningException.resourceNotFound("ModuleProgress", moduleProgressId);
		}

		final List<Repetition> repetitions = repetitionRepository
				.findByModuleProgressIdOrderByRepetitionOrder(moduleProgressId);
		return repetitionMapper.toDtoList(repetitions);
	}

	@Override
	@Transactional(readOnly = true)
	public RepetitionResponse findByModuleProgressIdAndOrder(UUID moduleProgressId, RepetitionOrder repetitionOrder) {
		log.debug("Finding repetition by module progress ID: {} and order: {}", moduleProgressId, repetitionOrder);

		final Repetition repetition = repetitionRepository
				.findByModuleProgressIdAndRepetitionOrder(moduleProgressId, repetitionOrder)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(
						"Repetition for ModuleProgress " + moduleProgressId + " and Order " + repetitionOrder, null));

		return repetitionMapper.toDto(repetition);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RepetitionResponse> findDueRepetitions(UUID userId, LocalDate reviewDate, RepetitionStatus status,
			Pageable pageable) {
		log.debug("Finding repetitions due for review for user ID: {} on or before date: {}, status: {}, pageable: {}",
				userId, reviewDate, status, pageable);

		final LocalDate dateToCheck = reviewDate != null ? reviewDate : LocalDate.now();
		final RepetitionStatus statusToCheck = status != null ? status : RepetitionStatus.NOT_STARTED;

		return repetitionRepository.findDueRepetitions(userId, dateToCheck, statusToCheck, pageable)
				.map(repetitionMapper::toDto);
	}

	@Override
	@Transactional
	public RepetitionResponse update(UUID id, RepetitionUpdateRequest request) {
		log.debug("Updating repetition with ID: {}, request: {}", id, request);

		final Repetition repetition = repetitionRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));

		repetitionMapper.updateFromDto(request, repetition);
		final Repetition updatedRepetition = repetitionRepository.save(repetition);

		log.info("Repetition updated successfully with ID: {}", updatedRepetition.getId());
		return repetitionMapper.toDto(updatedRepetition);
	}
}
