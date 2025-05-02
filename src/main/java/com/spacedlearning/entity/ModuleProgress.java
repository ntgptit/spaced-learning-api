package com.spacedlearning.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.spacedlearning.entity.enums.CycleStudied;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Entity representing a user's progress for a specific module.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "module_progress", schema = "spaced_learning")
@Slf4j
public class ModuleProgress extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "first_learning_date")
    private LocalDate firstLearningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycles_studied", length = 30)
    private CycleStudied cyclesStudied = CycleStudied.FIRST_TIME;

    @Column(name = "next_study_date")
    private LocalDate nextStudyDate;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "percent_complete", precision = 5, scale = 2)
    private BigDecimal percentComplete = BigDecimal.ZERO;

    @OneToMany(mappedBy = "moduleProgress", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repetition> repetitions = new ArrayList<>();

    @OneToMany(mappedBy = "moduleProgress", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningCycle> learningCycles = new ArrayList<>();

    public Repetition addRepetition(Repetition repetition) {
        repetitions.add(repetition);
        repetition.setModuleProgress(this);
        return repetition;
    }

    public boolean removeRepetition(Repetition repetition) {
        final boolean removed = repetitions.remove(repetition);
        if (removed) {
            repetition.setModuleProgress(null);
        }
        return removed;
    }

    public LocalDate findLatestCycleStart(CycleStudied cycle) {
        return learningCycles.stream()
                .filter(r -> r.getCycle() == cycle)
                .map(LearningCycle::getStartDate)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    public LocalDate getEffectiveStartDate() {
        final LocalDate cycleStart = findLatestCycleStart(cyclesStudied);
        if (cycleStart != null) {
            return cycleStart;
        }

        if (firstLearningDate != null) {
            return firstLearningDate;
        }

        log.warn("Missing effective start date for moduleProgress ID: {}. Fallback to now.", getId());
        return LocalDate.now();
    }

    public void addCycleStart(CycleStudied cycle, LocalDate date) {
        final LearningCycle learningCycle = new LearningCycle();
        learningCycle.setModuleProgress(this);
        learningCycle.setCycle(cycle);
        learningCycle.setStartDate(date);
        learningCycles.add(learningCycle);
    }
}