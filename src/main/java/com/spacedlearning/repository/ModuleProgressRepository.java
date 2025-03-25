// File: src/main/java/com/spacedlearning/repository/ModuleProgressRepository.java
package com.spacedlearning.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.ModuleProgress;

/**
 * Repository for ModuleProgress entity
 */
@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, UUID> {

	/**
	 * Count progress records by user and book
	 * 
	 * @param userId User ID
	 * @param bookId Book ID
	 * @return Number of progress records
	 */
	@Query("SELECT COUNT(mp) FROM ModuleProgress mp WHERE mp.user.id = :userId " + "AND mp.module.book.id = :bookId")
	long countByUserAndBook(@Param("userId") UUID userId, @Param("bookId") UUID bookId);

	/**
	 * Check if progress exists for user and module
	 * 
	 * @param userId   User ID
	 * @param moduleId Module ID
	 * @return true if exists, false otherwise
	 */
	boolean existsByUserIdAndModuleId(UUID userId, UUID moduleId);

	/**
	 * Find progress by module ID
	 * 
	 * @param moduleId Module ID
	 * @param pageable Pagination information
	 * @return Page of progress records
	 */
	Page<ModuleProgress> findByModuleId(UUID moduleId, Pageable pageable);

	/**
	 * Find progress records by user and book
	 * 
	 * @param userId   User ID
	 * @param bookId   Book ID
	 * @param pageable Pagination information
	 * @return Page of progress records
	 */
	@Query("SELECT mp FROM ModuleProgress mp WHERE mp.user.id = :userId " + "AND mp.module.book.id = :bookId "
			+ "ORDER BY mp.module.moduleNo ASC")
	Page<ModuleProgress> findByUserAndBook(@Param("userId") UUID userId, @Param("bookId") UUID bookId,
			Pageable pageable);

	/**
	 * Find progress by user ID
	 * 
	 * @param userId   User ID
	 * @param pageable Pagination information
	 * @return Page of progress records
	 */
	Page<ModuleProgress> findByUserId(UUID userId, Pageable pageable);

	/**
	 * Find progress by user ID and module ID
	 * 
	 * @param userId   User ID
	 * @param moduleId Module ID
	 * @return Optional containing progress record
	 */
	Optional<ModuleProgress> findByUserIdAndModuleId(UUID userId, UUID moduleId);

	/**
	 * Find progress records due for study on or before a specific date
	 * 
	 * @param userId    User ID
	 * @param studyDate Study date
	 * @param pageable  Pagination information
	 * @return Page of progress records
	 */
	@Query("SELECT mp FROM ModuleProgress mp WHERE mp.user.id = :userId " + "AND mp.nextStudyDate <= :studyDate "
			+ "ORDER BY mp.nextStudyDate ASC")
	Page<ModuleProgress> findDueForStudy(@Param("userId") UUID userId, @Param("studyDate") LocalDate studyDate,
			Pageable pageable);

	/**
	 * Find progress by ID with repetitions eagerly loaded
	 * 
	 * @param id Progress ID
	 * @return Optional containing progress with repetitions
	 */
	@EntityGraph(attributePaths = { "repetitions" })
	Optional<ModuleProgress> findWithRepetitionsById(UUID id);
}