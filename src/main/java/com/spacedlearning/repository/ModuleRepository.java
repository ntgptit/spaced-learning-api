// File: src/main/java/com/spacedlearning/repository/ModuleRepository.java
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
 * Repository for Module entity
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
}