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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "learning_cycles", schema = "spaced_learning", uniqueConstraints = @UniqueConstraint(columnNames = {
        "module_progress_id", "cycles_studied", "start_date" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "moduleProgress")
public class LearningCycle extends BaseEntity {

    /**
     * Creates a new learning cycle for the specified module progress.
     */
    public static LearningCycle create(ModuleProgress moduleProgress, CycleStudied cycle, LocalDate startDate) {
        return LearningCycle.builder()
                .moduleProgress(moduleProgress)
                .cycle(cycle)
                .startDate(startDate)
                .build();
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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
}
