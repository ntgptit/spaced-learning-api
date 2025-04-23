package com.spacedlearning.repository;

import com.spacedlearning.entity.ModuleProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ModuleProgress entity
 */
@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, UUID> {

    /**
     * Check if progress exists for module
     *
     * @param moduleId Module ID
     * @return true if exists, false otherwise
     */
    boolean existsByModuleId(UUID moduleId);

    /**
     * Find progress by module ID
     *
     * @param moduleId Module ID
     * @param pageable Pagination information
     * @return Page of progress records
     */
    Page<ModuleProgress> findByModuleId(UUID moduleId, Pageable pageable);

    /**
     * Find progress records by book
     *
     * @param bookId   Book ID
     * @param pageable Pagination information
     * @return Page of progress records
     */
    @Query("SELECT mp FROM ModuleProgress mp WHERE mp.module.book.id = :bookId ORDER BY mp.module.moduleNo ASC")
    Page<ModuleProgress> findByBookId(@Param("bookId") UUID bookId, Pageable pageable);

    /**
     * Find progress by module ID
     *
     * @param moduleId Module ID
     * @return Optional containing progress record
     */
    Optional<ModuleProgress> findByModuleId(UUID moduleId);

    /**
     * Find progress records due for study on or before a specific date
     *
     * @param studyDate Study date
     * @param pageable  Pagination information
     * @return Page of progress records
     */
    @Query("SELECT mp FROM ModuleProgress mp WHERE mp.nextStudyDate <= :studyDate ORDER BY mp.nextStudyDate ASC")
    Page<ModuleProgress> findByNextStudyDateLessThanEqual(@Param("studyDate") LocalDate studyDate, Pageable pageable);

    /**
     * Find progress by ID with repetitions eagerly loaded
     *
     * @param id Progress ID
     * @return Optional containing progress with repetitions
     */
    @EntityGraph(attributePaths = {"repetitions"})
    Optional<ModuleProgress> findWithRepetitionsById(UUID id);

}