package com.spacedlearning.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ModuleProgressMapper extends AbstractGenericMapper<ModuleProgress, ModuleProgressDetailResponse> {

    private final RepetitionMapper repetitionMapper;

    @Override
    protected ModuleProgress mapDtoToEntity(final ModuleProgressDetailResponse dto, final ModuleProgress entity) {
        if ((dto == null) || (entity == null)) {
            return entity;
        }

        if (dto.getFirstLearningDate() != null) {
            entity.setFirstLearningDate(dto.getFirstLearningDate());
        }
        if (dto.getCyclesStudied() != null) {
            entity.setCyclesStudied(dto.getCyclesStudied());
        }
        if (dto.getNextStudyDate() != null) {
            entity.setNextStudyDate(dto.getNextStudyDate());
        }
        if (dto.getPercentComplete() != null) {
            entity.setPercentComplete(dto.getPercentComplete());
        }

        return entity;
    }

    @Override
    protected ModuleProgressDetailResponse mapToDto(final ModuleProgress entity) {
        if (entity == null) {
            return null;
        }

        final var repetitions = this.repetitionMapper.toDtoList(entity.getRepetitions());

        return ModuleProgressDetailResponse.builder()
                .id(entity.getId())
                .moduleId(entity.getModule().getId())
                .moduleTitle(entity.getModule().getTitle())
                .moduleUrl(entity.getModule().getUrl()) // Mapping URL từ Module entity
                .userId(null) // Giữ là null vì đã bỏ userId ở ModuleProgress
                .userName(null) // Giữ là null vì đã bỏ userName ở ModuleProgress
                .firstLearningDate(entity.getFirstLearningDate())
                .cyclesStudied(entity.getCyclesStudied())
                .nextStudyDate(entity.getNextStudyDate())
                .percentComplete(entity.getPercentComplete())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .repetitions(repetitions)
                .build();
    }

    @Override
    protected ModuleProgress mapToEntity(final ModuleProgressDetailResponse dto) {
        if (dto == null) {
            return null;
        }

        final var progress = new ModuleProgress();
        progress.setFirstLearningDate(dto.getFirstLearningDate());
        progress.setCyclesStudied(dto.getCyclesStudied());
        progress.setNextStudyDate(dto.getNextStudyDate());
        progress.setPercentComplete(dto.getPercentComplete());
        progress.setRepetitions(Collections.emptyList());

        return progress;
    }

    public ModuleProgress toEntity(final ModuleProgressCreateRequest request, final Module module) {
        if (request == null) {
            return null;
        }
        Objects.requireNonNull(module, "Module must not be null");

        final var progress = new ModuleProgress();
        progress.setModule(module);
        progress.setFirstLearningDate(request.getFirstLearningDate());
        progress.setCyclesStudied(request.getCyclesStudied());
        progress.setNextStudyDate(request.getNextStudyDate());
        progress.setPercentComplete(request.getPercentComplete());
        progress.setRepetitions(Collections.emptyList());

        return progress;
    }

    public ModuleProgressSummaryResponse toSummaryDto(final ModuleProgress entity) {
        if (entity == null) {
            return null;
        }

        return ModuleProgressSummaryResponse.builder()
                .id(entity.getId())
                .moduleId(entity.getModule().getId())
                .firstLearningDate(entity.getFirstLearningDate())
                .cyclesStudied(entity.getCyclesStudied())
                .nextStudyDate(entity.getNextStudyDate())
                .percentComplete(entity.getPercentComplete())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .repetitionCount(CollectionUtils.size(entity.getRepetitions()))
                .build();
    }

    public List<ModuleProgressSummaryResponse> toSummaryDtoList(final List<ModuleProgress> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toSummaryDto)
                .toList();
    }

    public ModuleProgress updateFromDto(final ModuleProgressUpdateRequest request, final ModuleProgress entity) {
        if ((request == null) || (entity == null)) {
            return entity;
        }

        if (request.getFirstLearningDate() != null) {
            entity.setFirstLearningDate(request.getFirstLearningDate());
        }
        if (request.getCyclesStudied() != null) {
            entity.setCyclesStudied(request.getCyclesStudied());
        }
        if (request.getNextStudyDate() != null) {
            entity.setNextStudyDate(request.getNextStudyDate());
        }
        if (request.getPercentComplete() != null) {
            entity.setPercentComplete(request.getPercentComplete());
        }

        return entity;
    }
}