// File: src/main/java/com/spacedlearning/repository/BookRepository.java
package com.spacedlearning.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;

/**
 * Repository for Book entity
 */
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

	/**
	 * Find all categories from books
	 * 
	 * @return List of unique categories
	 */
	@Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL ORDER BY b.category")
	List<String> findAllCategories();

	/**
	 * Find published books with published status and optional filtering
	 * 
	 * @param status          Book status (optional)
	 * @param difficultyLevel Difficulty level (optional)
	 * @param category        Category (optional)
	 * @param pageable        Pagination information
	 * @return Page of books
	 */
	@Query("SELECT b FROM Book b WHERE " + "(:status IS NULL OR b.status = :status) "
			+ "AND (:difficultyLevel IS NULL OR b.difficultyLevel = :difficultyLevel) "
			+ "AND (:category IS NULL OR b.category = :category)")
	Page<Book> findBooksByFilters(@Param("status") BookStatus status,
			@Param("difficultyLevel") DifficultyLevel difficultyLevel, @Param("category") String category,
			Pageable pageable);

	/**
	 * Find books by category
	 * 
	 * @param category Category
	 * @param pageable Pagination information
	 * @return Page of books
	 */
	Page<Book> findByCategory(String category, Pageable pageable);

	/**
	 * Find books by difficulty level
	 * 
	 * @param difficultyLevel Difficulty level
	 * @param pageable        Pagination information
	 * @return Page of books
	 */
	Page<Book> findByDifficultyLevel(DifficultyLevel difficultyLevel, Pageable pageable);

	/**
	 * Find books by name containing the search term
	 * 
	 * @param searchTerm Search term
	 * @param pageable   Pagination information
	 * @return Page of books
	 */
	Page<Book> findByNameContainingIgnoreCase(String searchTerm, Pageable pageable);

	/**
	 * Find books by status
	 * 
	 * @param status   Book status
	 * @param pageable Pagination information
	 * @return Page of books
	 */
	Page<Book> findByStatus(BookStatus status, Pageable pageable);

	/**
	 * Find book by ID with modules eagerly loaded
	 * 
	 * @param id Book ID
	 * @return Optional containing book with modules
	 */
	@EntityGraph(attributePaths = { "modules" })
	Optional<Book> findWithModulesById(UUID id);
}
