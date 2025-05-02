package com.spacedlearning.entity;

import java.time.LocalDate;

import com.spacedlearning.entity.enums.CycleStudied;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a learning cycle record for tracking study progress.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "learning_cycles", schema = "spaced_learning", uniqueConstraints = @UniqueConstraint(columnNames = {
        "module_progress_id", "cycles_studied", "start_date" }))
public class LearningCycle extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_progress_id", nullable = false)
    private ModuleProgress moduleProgress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "cycles_studied", length = 30, nullable = false)
    private CycleStudied cycle;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "description", length = 255)
    private String description;

    /**
     * Creates a new learning cycle for the specified module progress.
     *
     * @param moduleProgress The module progress this cycle belongs to
     * @param cycle          The cycle studied type
     * @param startDate      The start date of the cycle
     * @return The created learning cycle
     */
    public static LearningCycle create(ModuleProgress moduleProgress, CycleStudied cycle, LocalDate startDate) {
        final LearningCycle learningCycle = new LearningCycle();
        learningCycle.setModuleProgress(moduleProgress);
        learningCycle.setCycle(cycle);
        learningCycle.setStartDate(startDate);
        return learningCycle;
    }
}