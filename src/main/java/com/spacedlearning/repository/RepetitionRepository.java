package com.spacedlearning.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;

/**
 * Repository for Repetition entity with optimized query methods for statistics.
 */
@Repository
public interface RepetitionRepository extends JpaRepository<Repetition, UUID> {

    /**
     * Count repetitions by module progress ID and status
     *
     * @param moduleProgressId Module progress ID
     * @param status           Status
     * @return Number of repetitions
     */
    long countByModuleProgressIdAndStatus(UUID moduleProgressId, RepetitionStatus status);

    /**
     * Check if repetition exists for module progress and order
     *
     * @param moduleProgressId Module progress ID
     * @param repetitionOrder  Repetition order
     * @return true if exists, false otherwise
     */
    boolean existsByModuleProgressIdAndRepetitionOrder(UUID moduleProgressId, RepetitionOrder repetitionOrder);

    /**
     * Find repetition by module progress ID and order
     *
     * @param moduleProgressId Module progress ID
     * @param repetitionOrder  Repetition order
     * @return Optional containing repetition
     */
    Optional<Repetition> findByModuleProgressIdAndRepetitionOrder(UUID moduleProgressId,
            RepetitionOrder repetitionOrder);

    Optional<Repetition> findByModuleProgressIdAndRepetitionOrderAndStatus(UUID moduleProgressId,
            RepetitionOrder repetitionOrder, RepetitionStatus repetitionStatus);

    /**
     * Find repetitions by module progress ID and status, ordered by review date
     *
     * @param moduleProgressId Module progress ID
     * @param status           Status to filter by
     * @return List of repetitions ordered by review date
     */
    List<Repetition> findByModuleProgressIdAndStatusOrderByReviewDate(UUID moduleProgressId, RepetitionStatus status);

    /**
     * Find repetitions by module progress ID
     *
     * @param moduleProgressId Module progress ID
     * @return List of repetitions
     */
    List<Repetition> findByModuleProgressIdOrderByRepetitionOrder(UUID moduleProgressId);

    /**
     * Find repetitions due for review on or before a specific date
     *
     * @param reviewDate Review date
     * @param status     Status to filter by
     * @param pageable   Pagination information
     * @return Page of repetitions
     */
    @Query("SELECT r FROM Repetition r WHERE r.reviewDate <= :reviewDate AND r.status = :status ORDER BY r.reviewDate ASC")
    Page<Repetition> findDueRepetitions(@Param("reviewDate") LocalDate reviewDate,
            @Param("status") RepetitionStatus status, Pageable pageable);

    /**
     * Find repetitions due for review for a specific book
     *
     * @param bookId     Book ID
     * @param reviewDate Review date
     * @param status     Status to filter by
     * @param pageable   Pagination information
     * @return Page of repetitions
     */
    @Query("SELECT r FROM Repetition r JOIN r.moduleProgress mp JOIN mp.module m WHERE m.book.id = :bookId AND r.reviewDate <= :reviewDate AND r.status = :status ORDER BY r.reviewDate ASC")
    Page<Repetition> findDueRepetitionsByBook(@Param("bookId") UUID bookId, @Param("reviewDate") LocalDate reviewDate,
            @Param("status") RepetitionStatus status, Pageable pageable);

    /**
     * Count total repetitions by module progress ID
     *
     * @param moduleProgressId Module progress ID
     * @return Total number of repetitions
     */
    long countByModuleProgressId(UUID moduleProgressId);

    @Query(value = """
            SELECT
                COUNT(*)
            FROM
                spaced_learning.repetitions r
            WHERE
                r.review_date = :reviewDate
                AND r.status = 'NOT_STARTED'
            """, nativeQuery = true)
    int countReviewDateExisted(@Param("reviewDate") LocalDate reviewDate);

    /**
     * Finds the latest review date of completed repetitions for a module progress
     *
     * @param progressId ID of the module progress
     * @return Optional containing the latest review date or empty if none found
     */
    @Query("SELECT MAX(r.reviewDate) FROM Repetition r " +
            "WHERE r.moduleProgress.id = :progressId AND r.status = 'COMPLETED'")
    Optional<LocalDate> findLastCompletedRepetitionDate(@Param("progressId") UUID progressId);

    List<Repetition> findByModuleProgressIdAndStatusOrderByRepetitionOrder(
            @Param("progressId") UUID progressId,
            @Param("status") RepetitionStatus status);
}