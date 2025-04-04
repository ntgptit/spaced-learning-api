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

/**
 * Entity representing a user's progress for a specific module.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "module_progress", schema = "spaced_learning")
public class ModuleProgress extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    /**
     * Adds a repetition record to this progress and sets the bidirectional
     * relationship.
     *
     * @param repetition The repetition to add
     * @return The added repetition
     */
    public Repetition addRepetition(Repetition repetition) {
        repetitions.add(repetition);
        repetition.setModuleProgress(this);
        return repetition;
    }

    /**
     * Removes a repetition record from this progress.
     *
     * @param repetition The repetition to remove
     * @return True if the repetition was removed, false otherwise
     */
    public boolean removeRepetition(Repetition repetition) {
        final boolean removed = repetitions.remove(repetition);
        if (removed) {
            repetition.setModuleProgress(null);
        }
        return removed;
    }

//	/**
//     * Validate dates to ensure next_study_date is after first_learning_date.
//     */
//    @PrePersist
//    @PreUpdate
//    public void validateDates() {
//        if (firstLearningDate != null && nextStudyDate != null &&
//            nextStudyDate.isBefore(firstLearningDate)) {
//            throw new IllegalStateException("Next study date must be on or after first learning date");
//        }
//    }
}