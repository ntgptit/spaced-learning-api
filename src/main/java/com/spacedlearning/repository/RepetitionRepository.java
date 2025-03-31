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
	 * @param userId     User ID
	 * @param reviewDate Review date
	 * @param status     Status to filter by
	 * @param pageable   Pagination information
	 * @return Page of repetitions
	 */
	@Query("SELECT r FROM Repetition r WHERE r.moduleProgress.user.id = :userId " + "AND r.reviewDate <= :reviewDate "
			+ "AND r.status = :status " + "ORDER BY r.reviewDate ASC")
	Page<Repetition> findDueRepetitions(@Param("userId") UUID userId, @Param("reviewDate") LocalDate reviewDate,
			@Param("status") RepetitionStatus status, Pageable pageable);

	/**
	 * Count total repetitions by module progress ID
	 *
	 * @param moduleProgressId Module progress ID
	 * @return Total number of repetitions
	 */
	long countByModuleProgressId(UUID moduleProgressId);

	/**
	 * Count repetitions due today for a user
	 *
	 * @param userId User ID
	 * @return Number of repetitions due today
	 */
	@Query(value = """
			SELECT COUNT(r.id) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			WHERE mp.user_id = :userId
			AND r.review_date = CURRENT_DATE
			AND r.status = 'NOT_STARTED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int countDueTodayForUser(@Param("userId") UUID userId);

	/**
	 * Count repetitions due this week for a user
	 *
	 * @param userId User ID
	 * @return Number of repetitions due this week
	 */
	@Query(value = """
			SELECT COUNT(r.id) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			WHERE mp.user_id = :userId
			AND r.review_date BETWEEN CURRENT_DATE AND (CURRENT_DATE + INTERVAL '6 days')
			AND r.status = 'NOT_STARTED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int countDueThisWeekForUser(@Param("userId") UUID userId);

	/**
	 * Count repetitions due this month for a user
	 *
	 * @param userId User ID
	 * @return Number of repetitions due this month
	 */
	@Query(value = """
			SELECT COUNT(r.id) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			WHERE mp.user_id = :userId
			AND r.review_date BETWEEN CURRENT_DATE AND
			    (DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day')
			AND r.status = 'NOT_STARTED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int countDueThisMonthForUser(@Param("userId") UUID userId);

	/**
	 * Count total words in modules due today for a user
	 *
	 * @param userId User ID
	 * @return Total word count for modules due today
	 */
	@Query(value = """
			SELECT COALESCE(SUM(m.word_count), 0) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			JOIN spaced_learning.modules m ON mp.module_id = m.id
			WHERE mp.user_id = :userId
			AND r.review_date = CURRENT_DATE
			AND r.status = 'NOT_STARTED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			AND m.deleted_at IS NULL
			""", nativeQuery = true)
	int countWordsDueTodayForUser(@Param("userId") UUID userId);

	/**
	 * Count total words in modules due this week for a user
	 *
	 * @param userId User ID
	 * @return Total word count for modules due this week
	 */
	@Query(value = """
			SELECT COALESCE(SUM(m.word_count), 0) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			JOIN spaced_learning.modules m ON mp.module_id = m.id
			WHERE mp.user_id = :userId
			AND r.review_date BETWEEN CURRENT_DATE AND (CURRENT_DATE + INTERVAL '6 days')
			AND r.status = 'NOT_STARTED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			AND m.deleted_at IS NULL
			""", nativeQuery = true)
	int countWordsDueThisWeekForUser(@Param("userId") UUID userId);

	/**
	 * Count total words in modules due this month for a user
	 *
	 * @param userId User ID
	 * @return Total word count for modules due this month
	 */
	@Query(value = """
			SELECT COALESCE(SUM(m.word_count), 0) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			JOIN spaced_learning.modules m ON mp.module_id = m.id
			WHERE mp.user_id = :userId
			AND r.review_date BETWEEN CURRENT_DATE AND
			    (DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day')
			AND r.status = 'NOT_STARTED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			AND m.deleted_at IS NULL
			""", nativeQuery = true)
	int countWordsDueThisMonthForUser(@Param("userId") UUID userId);

	/**
	 * Count repetitions completed today for a user
	 *
	 * @param userId User ID
	 * @return Number of repetitions completed today
	 */
	@Query(value = """
			SELECT COUNT(r.id) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			WHERE mp.user_id = :userId
			AND CAST(r.updated_at AS DATE) = CURRENT_DATE
			AND r.status = 'COMPLETED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			""", nativeQuery = true)
	int countCompletedTodayForUser(@Param("userId") UUID userId);

	/**
	 * Count total words completed today for a user
	 *
	 * @param userId User ID
	 * @return Total word count for modules completed today
	 */
	@Query(value = """
			SELECT COALESCE(SUM(m.word_count), 0) FROM spaced_learning.repetitions r
			JOIN spaced_learning.module_progress mp ON r.module_progress_id = mp.id
			JOIN spaced_learning.modules m ON mp.module_id = m.id
			WHERE mp.user_id = :userId
			AND CAST(r.updated_at AS DATE) = CURRENT_DATE
			AND r.status = 'COMPLETED'
			AND r.deleted_at IS NULL
			AND mp.deleted_at IS NULL
			AND m.deleted_at IS NULL
			""", nativeQuery = true)
	int countWordsCompletedTodayForUser(@Param("userId") UUID userId);
}