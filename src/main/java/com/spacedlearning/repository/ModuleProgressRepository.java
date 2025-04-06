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
     * Find all progress by user ID without pagination
     *
     * @param userId User ID
     * @return List of progress records
     */
    List<ModuleProgress> findByUserId(UUID userId);

    /**
     * Find progress by user ID and module ID
     *
     * @param userId   User ID
     * @param moduleId Module ID
     * @return Optional containing progress record
     */
    Optional<ModuleProgress> findByUserIdAndModuleId(UUID userId, UUID moduleId);

    /**
     * Find progress by user ID and percent complete
     *
     * @param userId          User ID
     * @param percentComplete The completion percentage
     * @return List of progress records
     */
    List<ModuleProgress> findByUserIdAndPercentComplete(UUID userId, BigDecimal percentComplete);

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
     * Find progress records due for study on or before a specific date without pagination
     *
     * @param userId    User ID
     * @param studyDate Study date
     * @return List of progress records
     */
    @Query("SELECT mp FROM ModuleProgress mp WHERE mp.user.id = :userId " + "AND mp.nextStudyDate <= :studyDate "
            + "ORDER BY mp.nextStudyDate ASC")
    List<ModuleProgress> findDueForStudy(@Param("userId") UUID userId, @Param("studyDate") LocalDate studyDate);

    /**
     * Find progress by ID with repetitions eagerly loaded
     *
     * @param id Progress ID
     * @return Optional containing progress with repetitions
     */
    @EntityGraph(attributePaths = { "repetitions" })
    Optional<ModuleProgress> findWithRepetitionsById(UUID id);

    /**
     * Find unique book names for a user
     *
     * @param userId User ID
     * @return List of unique book names
     */
    @Query("SELECT DISTINCT m.book.name FROM Module m " +
            "JOIN ModuleProgress mp ON m.id = mp.module.id " +
            "WHERE mp.user.id = :userId " +
            "ORDER BY m.book.name")
    List<String> findUniqueBooksByUserId(@Param("userId") UUID userId);

    /**
     * Find progress by user ID and book name
     *
     * @param userId   User ID
     * @param bookName Book name
     * @return List of progress records
     */
    @Query("SELECT mp FROM ModuleProgress mp " +
            "JOIN mp.module m " +
            "JOIN m.book b " +
            "WHERE mp.user.id = :userId " +
            "AND b.name = :bookName")
    List<ModuleProgress> findByUserIdAndBookName(@Param("userId") UUID userId, @Param("bookName") String bookName);

    /**
     * Find progress by user ID and next study date
     *
     * @param userId        User ID
     * @param nextStudyDate Next study date
     * @return List of progress records
     */
    List<ModuleProgress> findByUserIdAndNextStudyDate(UUID userId, LocalDate nextStudyDate);

    /**
     * Find progress by user ID with repetitions eagerly loaded
     *
     * @param userId User ID
     * @return List of progress records with repetitions
     */
    @EntityGraph(attributePaths = { "repetitions", "module", "module.book" })
    @Query("SELECT mp FROM ModuleProgress mp WHERE mp.user.id = :userId")
    List<ModuleProgress> findByUserIdWithRepetitions(@Param("userId") UUID userId);

    /**
     * Calculate total learned words for a user
     *
     * @param userId User ID
     * @return Total learned words
     */
    @Query("SELECT SUM(m.wordCount * mp.percentComplete / 100) FROM ModuleProgress mp " +
            "JOIN mp.module m " +
            "WHERE mp.user.id = :userId")
    Integer calculateTotalLearnedWords(@Param("userId") UUID userId);
}