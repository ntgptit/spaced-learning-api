package com.spacedlearning.entity;

import java.time.LocalDate;

import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "repetitions", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "moduleProgress")
public class Repetition extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_progress_id", nullable = false)
    private ModuleProgress moduleProgress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "repetition_order", length = 20, nullable = false)
    private RepetitionOrder repetitionOrder;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private RepetitionStatus status = RepetitionStatus.NOT_STARTED;

    @Column(name = "review_date")
    private LocalDate reviewDate;
}
