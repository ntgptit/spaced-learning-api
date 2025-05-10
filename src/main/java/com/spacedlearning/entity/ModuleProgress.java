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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "module_progress", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = { "repetitions", "learningCycles" })
@EqualsAndHashCode(callSuper = true)
public class ModuleProgress extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "first_learning_date")
    private LocalDate firstLearningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycles_studied", length = 30, nullable = false)
    @Builder.Default
    private CycleStudied cyclesStudied = CycleStudied.FIRST_TIME;

    @Column(name = "next_study_date")
    private LocalDate nextStudyDate;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "percent_complete", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal percentComplete = BigDecimal.ZERO;

    /**
     * The number of extended review cycles performed after reaching MORE_THAN_THREE_REVIEWS.
     */
    @Column(name = "extended_review_count", nullable = true)
    @Builder.Default
    private Integer extendedReviewCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "moduleProgress", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repetition> repetitions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "moduleProgress", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningCycle> learningCycles = new ArrayList<>();

    public void addCycleStart(CycleStudied cycle, LocalDate date) {
        final var learningCycle = new LearningCycle();
        learningCycle.setModuleProgress(this);
        learningCycle.setCycle(cycle);
        learningCycle.setStartDate(date);
        this.learningCycles.add(learningCycle);
    }

    public Repetition addRepetition(Repetition repetition) {
        this.repetitions.add(repetition);
        repetition.setModuleProgress(this);
        return repetition;
    }

    public LocalDate findLatestCycleStart(CycleStudied cycle) {
        return this.learningCycles.stream()
                .filter(r -> r.getCycle() == cycle)
                .map(LearningCycle::getStartDate)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    public LocalDate getEffectiveStartDate() {
        final var cycleStart = findLatestCycleStart(this.cyclesStudied);
        if (cycleStart != null) {
            return cycleStart;
        }
        if (this.firstLearningDate != null) {
            return this.firstLearningDate;
        }
        return LocalDate.now(); // fallback
    }

    public boolean removeRepetition(Repetition repetition) {
        final var removed = this.repetitions.remove(repetition);
        if (removed) {
            repetition.setModuleProgress(null);
        }
        return removed;
    }
}
