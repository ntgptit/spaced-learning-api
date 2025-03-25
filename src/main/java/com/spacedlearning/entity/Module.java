package com.spacedlearning.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a module in a book.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "modules", schema = "spaced_learning")
public class Module extends BaseEntity {

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@NotNull
	@Min(1)
	@Column(name = "module_no", nullable = false)
	private Integer moduleNo;

	@NotBlank
	@Size(max = 255)
	@Column(name = "title", length = 255, nullable = false)
	private String title;

	@Min(0)
	@Column(name = "word_count")
	private Integer wordCount = 0;

	@OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ModuleProgress> progress = new ArrayList<>();

	/**
	 * Adds a progress record to this module and sets the bidirectional
	 * relationship.
	 *
	 * @param moduleProgress The progress to add
	 * @return The added progress
	 */
	public ModuleProgress addProgress(ModuleProgress moduleProgress) {
		progress.add(moduleProgress);
		moduleProgress.setModule(this);
		return moduleProgress;
	}

	/**
	 * Removes a progress record from this module.
	 *
	 * @param moduleProgress The progress to remove
	 * @return True if the progress was removed, false otherwise
	 */
	public boolean removeProgress(ModuleProgress moduleProgress) {
		final boolean removed = progress.remove(moduleProgress);
		if (removed) {
			moduleProgress.setModule(null);
		}
		return removed;
	}
}