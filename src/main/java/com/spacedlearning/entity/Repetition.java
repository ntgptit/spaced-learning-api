package com.spacedlearning.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a repetition record for spaced learning.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repetitions", schema = "spaced_learning")
public class Repetition extends BaseEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lesson_progress_id", nullable = false)
	private ModuleProgress moduleProgress;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "repetition_order", length = 20, nullable = false)
	private RepetitionOrder repetitionOrder;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	private RepetitionStatus status = RepetitionStatus.NOT_STARTED;

	@Column(name = "review_date")
	private LocalDate reviewDate;


}
