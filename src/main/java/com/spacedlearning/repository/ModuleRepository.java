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

import com.spacedlearning.entity.Module;

/**
 * Repository for Module entity with optimized queries
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {

	/**
	 * Count modules by book ID
	 * 
	 * @param bookId Book ID
	 * @return Number of modules
	 */
	@Query("SELECT COUNT(m) FROM Module m WHERE m.book.id = :bookId")
	long countByBookId(@Param("bookId") UUID bookId);

	/**
	 * Check if a module with the given number exists for a book
	 * 
	 * @param bookId   Book ID
	 * @param moduleNo Module number
	 * @return true if exists, false otherwise
	 */
	boolean existsByBookIdAndModuleNo(UUID bookId, Integer moduleNo);

	/**
	 * Find modules by book ID (paginated)
	 * 
	 * @param bookId   Book ID
	 * @param pageable Pagination information
	 * @return Page of modules
	 */
	Page<Module> findByBookId(UUID bookId, Pageable pageable);

	/**
	 * Find module by book ID and module number
	 * 
	 * @param bookId   Book ID
	 * @param moduleNo Module number
	 * @return Optional containing the module
	 */
	Optional<Module> findByBookIdAndModuleNo(UUID bookId, Integer moduleNo);

	/**
	 * Find modules by book ID
	 * 
	 * @param bookId Book ID
	 * @return List of modules
	 */
	List<Module> findByBookIdOrderByModuleNo(UUID bookId);

	/**
	 * Find the latest module number for a book
	 * 
	 * @param bookId Book ID
	 * @return Maximum module number or 0 if no modules
	 */
	@Query("SELECT COALESCE(MAX(m.moduleNo), 0) FROM Module m WHERE m.book.id = :bookId")
	Integer findMaxModuleNoByBookId(@Param("bookId") UUID bookId);

	/**
	 * Find module by ID with progress eagerly loaded
	 * 
	 * @param id Module ID
	 * @return Optional containing module with progress
	 */
	@EntityGraph(attributePaths = { "progress" })
	Optional<Module> findWithProgressById(UUID id);

	/**
	 * Count completed modules for user
	 * 
	 * @param userId User ID
	 * @return Number of completed modules
	 */
	@Query(value = """
			SELECT COUNT(DISTINCT m.id) FROM spaced_learning.modules m
			JOIN spaced_learning.module_progress mp ON m.id = mp.module_id
			WHERE mp.user_id = :userId
			AND mp.percent_complete = 100
			AND m.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int countCompletedModulesForUser(@Param("userId") UUID userId);

	/**
	 * Count in-progress modules for user
	 * 
	 * @param userId User ID
	 * @return Number of in-progress modules
	 */
	@Query(value = """
			SELECT COUNT(DISTINCT m.id) FROM spaced_learning.modules m
			JOIN spaced_learning.module_progress mp ON m.id = mp.module_id
			WHERE mp.user_id = :userId
			AND mp.percent_complete > 0
			AND mp.percent_complete < 100
			AND m.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int countInProgressModulesForUser(@Param("userId") UUID userId);

	/**
	 * Count total modules for user
	 * 
	 * @param userId User ID
	 * @return Total number of modules
	 */
	@Query(value = """
			SELECT COUNT(DISTINCT m.id) FROM spaced_learning.modules m
			JOIN spaced_learning.module_progress mp ON m.id = mp.module_id
			WHERE mp.user_id = :userId
			AND m.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int countTotalModulesForUser(@Param("userId") UUID userId);

	/**
	 * Get total word count for a user
	 * 
	 * @param userId User ID
	 * @return Total word count
	 */
	@Query(value = """
			SELECT COALESCE(SUM(m.word_count), 0) FROM spaced_learning.modules m
			JOIN spaced_learning.module_progress mp ON m.id = mp.module_id
			WHERE mp.user_id = :userId
			AND m.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int getTotalWordCountForUser(@Param("userId") UUID userId);

	/**
	 * Get total learned word count for a user based on module progress
	 * 
	 * @param userId User ID
	 * @return Total learned word count
	 */
	@Query(value = """
			SELECT COALESCE(SUM(CASE
			    WHEN mp.percent_complete = 100 THEN m.word_count
			    ELSE CAST((m.word_count * mp.percent_complete / 100) AS INT)
			END), 0)
			FROM spaced_learning.modules m
			JOIN spaced_learning.module_progress mp ON m.id = mp.module_id
			WHERE mp.user_id = :userId
			AND m.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int getLearnedWordCountForUser(@Param("userId") UUID userId);
}