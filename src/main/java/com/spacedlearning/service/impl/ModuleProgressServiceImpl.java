// File: src/main/java/com/spacedlearning/service/impl/ModuleProgressServiceImpl.java
package com.spacedlearning.service.impl;


import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.ModuleProgressMapper;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.ModuleProgressService;
import com.spacedlearning.service.RepetitionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of ModuleProgressService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleProgressServiceImpl implements ModuleProgressService {

	private final ModuleProgressRepository progressRepository;
	private final ModuleRepository moduleRepository;
	private final UserRepository userRepository;
	private final ModuleProgressMapper progressMapper;
	private final RepetitionService repetitionService;

	@Override
	@Transactional
//	@CacheEvict(value = { "userModuleProgress", "moduleProgress" }, allEntries = true)
	public ModuleProgressDetailResponse create(ModuleProgressCreateRequest request) {
		log.debug("Creating new module progress: {}", request);

		// Validate module and user existence
		final Module module = moduleRepository.findById(request.getModuleId())
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Module", request.getModuleId()));

		final User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("User", request.getUserId()));

		// Check if progress already exists
		if (progressRepository.existsByUserIdAndModuleId(request.getUserId(), request.getModuleId())) {
			throw SpacedLearningException.resourceAlreadyExists("ModuleProgress", "user_id and module_id",
					request.getUserId() + ", " + request.getModuleId());
		}

		// Create and save progress
		final ModuleProgress progress = progressMapper.toEntity(request, module, user);
		// Continuing:
		// src/main/java/com/spacedlearning/service/impl/ModuleProgressServiceImpl.java
		final ModuleProgress savedProgress = progressRepository.save(progress);

		log.info("Module progress created successfully with ID: {}", savedProgress.getId());

		// Create default repetition schedule
		repetitionService.createDefaultSchedule(savedProgress.getId());

		return progressMapper.toDto(savedProgress);
	}

	@Override
	@Transactional
//	@CacheEvict(value = { "userModuleProgress", "moduleProgress" }, key = "#id")
	public void delete(UUID id) {
		log.debug("Deleting module progress with ID: {}", id);

		final ModuleProgress progress = progressRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("ModuleProgress", id));

		progress.softDelete(); // Use soft delete
		progressRepository.save(progress);

		log.info("Module progress soft deleted successfully with ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleProgressSummaryResponse> findAll(Pageable pageable) {
		log.debug("Finding all module progress with pagination: {}", pageable);
		return progressRepository.findAll(pageable).map(progressMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
//	@Cacheable(value = "moduleProgress", key = "#id")
	public ModuleProgressDetailResponse findById(UUID id) {
		log.debug("Finding module progress by ID: {}", id);
		final ModuleProgress progress = progressRepository.findWithRepetitionsById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("ModuleProgress", id));
		return progressMapper.toDto(progress);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleProgressSummaryResponse> findByModuleId(UUID moduleId, Pageable pageable) {
		log.debug("Finding module progress by module ID: {}, pageable: {}", moduleId, pageable);
		// Verify module exists
		if (!moduleRepository.existsById(moduleId)) {
			throw SpacedLearningException.resourceNotFound("Module", moduleId);
		}

		return progressRepository.findByModuleId(moduleId, pageable).map(progressMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleProgressSummaryResponse> findByUserId(UUID userId, Pageable pageable) {
		log.debug("Finding module progress by user ID: {}, pageable: {}", userId, pageable);
		// Verify user exists
		if (!userRepository.existsById(userId)) {
			throw SpacedLearningException.resourceNotFound("User", userId);
		}

		return progressRepository.findByUserId(userId, pageable).map(progressMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleProgressSummaryResponse> findByUserIdAndBookId(UUID userId, UUID bookId, Pageable pageable) {
		log.debug("Finding module progress by user ID: {} and book ID: {}, pageable: {}", userId, bookId, pageable);

		// Verify user exists
		if (!userRepository.existsById(userId)) {
			throw SpacedLearningException.resourceNotFound("User", userId);
		}

		return progressRepository.findByUserAndBook(userId, bookId, pageable).map(progressMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
//	@Cacheable(value = "userModuleProgress", key = "#userId + '_' + #moduleId")
	public ModuleProgressDetailResponse findByUserIdAndModuleId(UUID userId, UUID moduleId) {
		log.debug("Finding module progress by user ID: {} and module ID: {}", userId, moduleId);

		final ModuleProgress progress = progressRepository.findByUserIdAndModuleId(userId, moduleId)
				.orElseThrow(() -> SpacedLearningException
						.resourceNotFound("ModuleProgress for User " + userId + " and Module " + moduleId, null));

		return progressMapper.toDto(progress);
	}

	@Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findDueForStudy(UUID userId, LocalDate studyDate, Pageable pageable) {
        log.debug("Finding module progress due for study for user ID: {} on or before date: {}, pageable: {}",
                userId, studyDate, pageable);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw SpacedLearningException.resourceNotFound("User", userId);
        }

        final LocalDate dateToCheck = studyDate != null ? studyDate : LocalDate.now();

        return progressRepository.findDueForStudy(userId, dateToCheck, pageable)
                .map(progressMapper::toSummaryDto);
    }

	@Override
	@Transactional
//	@CacheEvict(value = { "userModuleProgress", "moduleProgress" }, key = "#id")
	public ModuleProgressDetailResponse update(UUID id, ModuleProgressUpdateRequest request) {
		log.debug("Updating module progress with ID: {}, request: {}", id, request);

		final ModuleProgress progress = progressRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("ModuleProgress", id));

		progressMapper.updateFromDto(request, progress);
		final ModuleProgress updatedProgress = progressRepository.save(progress);

		log.info("Module progress updated successfully with ID: {}", updatedProgress.getId());
		return progressMapper.toDto(updatedProgress);
	}
}