package com.spacedlearning.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.spacedlearning.entity.ModuleProgress;

/**
 * Repository for ModuleProgress entity
 */
@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, UUID> {

    /**
     * Count progress records by book
     *
     * @param bookId Book ID
     * @return Number of progress records
     */
    @Query("SELECT COUNT(mp) FROM ModuleProgress mp WHERE mp.module.book.id = :bookId")
    long countByBookId(@Param("bookId") UUID bookId);

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
     * Find progress by percent complete
     *
     * @param percentComplete The completion percentage
     * @return List of progress records
     */
    List<ModuleProgress> findByPercentComplete(BigDecimal percentComplete);

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
     * Find progress records due for study on or before a specific date without pagination
     *
     * @param studyDate Study date
     * @return List of progress records
     */
    @Query("SELECT mp FROM ModuleProgress mp WHERE mp.nextStudyDate <= :studyDate ORDER BY mp.nextStudyDate ASC")
    List<ModuleProgress> findByNextStudyDateLessThanEqual(@Param("studyDate") LocalDate studyDate);

    /**
     * Find progress by ID with repetitions eagerly loaded
     *
     * @param id Progress ID
     * @return Optional containing progress with repetitions
     */
    @EntityGraph(attributePaths = { "repetitions" })
    Optional<ModuleProgress> findWithRepetitionsById(UUID id);

    /**
     * Find unique book names
     *
     * @return List of unique book names
     */
    @Query("SELECT DISTINCT m.book.name FROM Module m JOIN ModuleProgress mp ON m.id = mp.module.id ORDER BY m.book.name")
    List<String> findUniqueBookNames();

    /**
     * Find progress by book name
     *
     * @param bookName Book name
     * @return List of progress records
     */
    @Query("SELECT mp FROM ModuleProgress mp JOIN mp.module m JOIN m.book b WHERE b.name = :bookName")
    List<ModuleProgress> findByBookName(@Param("bookName") String bookName);

    /**
     * Find progress by next study date
     *
     * @param nextStudyDate Next study date
     * @return List of progress records
     */
    List<ModuleProgress> findByNextStudyDate(LocalDate nextStudyDate);

    /**
     * Calculate total learned words across all progress
     *
     * @return Total learned words
     */
    @Query("SELECT SUM(m.wordCount * mp.percentComplete / 100) FROM ModuleProgress mp JOIN mp.module m")
    Integer calculateTotalLearnedWords();

    /**
     * Find progress by module ID with repetitions eagerly loaded
     *
     * @param moduleId Module ID
     * @return Optional containing progress with repetitions
     */
    @EntityGraph(attributePaths = { "repetitions" })
    Optional<ModuleProgress> findWithRepetitionsByModuleId(UUID moduleId);
}