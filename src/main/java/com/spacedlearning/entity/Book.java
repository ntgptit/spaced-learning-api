package com.spacedlearning.entity;

import java.util.ArrayList;
import java.util.List;

import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a book in the system.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books", schema = "spaced_learning")
public class Book extends BaseEntity {

	@NotBlank
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20)
	private BookStatus status = BookStatus.DRAFT;

	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty_level", length = 20)
	private DifficultyLevel difficultyLevel;

	@Size(max = 50)
	@Column(name = "category", length = 50)
	private String category;

	@Column(name = "book_no")
	private Integer bookNo = 0;

	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Module> modules = new ArrayList<>();

	/**
     * Adds a module to this book and sets the bidirectional relationship.
     *
     * @param module The module to add
     * @return The added module
     */
    public Module addModule(Module module) {
        modules.add(module);
        module.setBook(this);
        return module;
    }

	/**
     * Removes a module from this book.
     *
     * @param module The module to remove
     * @return True if the module was removed, false otherwise
     */
    public boolean removeModule(Module module) {
        final boolean removed = modules.remove(module);
        if (removed) {
            module.setBook(null);
        }
        return removed;
    }
}