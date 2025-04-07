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
     * Count total modules
     *
     * @param userId User ID
     * @return Total number of modules
     */
    @Query(value = """
            SELECT
                COUNT(*)
            FROM
                spaced_learning.modules m
            LEFT JOIN spaced_learning.books b ON
                m.book_id = b.id
            WHERE
                m.deleted_at IS NULL
                AND b.deleted_at IS NULL
            			""", nativeQuery = true)
    int countTotalModules();

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

    @Query(value = """
                WITH cycle_types AS (
                    SELECT 'FIRST_TIME' AS cycle_name
                    UNION ALL SELECT 'FIRST_REVIEW'
                    UNION ALL SELECT 'SECOND_REVIEW'
                    UNION ALL SELECT 'THIRD_REVIEW'
                    UNION ALL SELECT 'MORE_THAN_THREE_REVIEWS'
                )
                SELECT
                    ct.cycle_name AS cycles_studied,
                    COALESCE(COUNT(mp.cycles_studied), 0) AS cycle_count
                FROM
                    cycle_types ct
                LEFT JOIN (
                    spaced_learning.module_progress mp
                    INNER JOIN spaced_learning.modules m
                        ON m.id = mp.module_id
                    INNER JOIN spaced_learning.books b
                        ON m.book_id = b.id
                        AND b.deleted_at IS NULL
                        AND m.deleted_at IS NULL
                ) ON ct.cycle_name = mp.cycles_studied
                GROUP BY
                    ct.cycle_name
                ORDER BY
                    CASE ct.cycle_name
                        WHEN 'FIRST_TIME' THEN 1
                        WHEN 'FIRST_REVIEW' THEN 2
                        WHEN 'SECOND_REVIEW' THEN 3
                        WHEN 'THIRD_REVIEW' THEN 4
                        WHEN 'MORE_THAN_THREE_REVIEWS' THEN 5
                    END
            """, nativeQuery = true)
    List<Object[]> getModuleCycleStudiedStats();

    @Query(value = """
            SELECT
                COUNT(DISTINCT mp.module_id) AS users_with_overdue_modules
            FROM
                spaced_learning.module_progress mp
            JOIN
            spaced_learning.repetitions r ON
                r.module_progress_id = mp.id
            JOIN
            spaced_learning.modules m ON
                mp.module_id = m.id
            JOIN
            spaced_learning.books b ON
                m.book_id = b.id
            JOIN
            spaced_learning.users u ON
                u.id = mp.user_id
            WHERE
                r.review_date <= CURRENT_DATE
                AND mp.deleted_at IS NULL
                AND m.deleted_at IS NULL
                AND b.deleted_at IS NULL
                AND mp.user_id = :userId
            """, nativeQuery = true)
    int countDueTodayForUser(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
                COUNT(DISTINCT mp.module_id) AS users_with_due_modules
            FROM
                spaced_learning.module_progress mp
            JOIN
            spaced_learning.repetitions r ON
                r.module_progress_id = mp.id
            JOIN
            spaced_learning.modules m ON
                mp.module_id = m.id
            JOIN
            spaced_learning.books b ON
                m.book_id = b.id
            JOIN
            spaced_learning.users u ON
                u.id = mp.user_id
            WHERE
                r.review_date <= DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '6 days'
                AND mp.deleted_at IS NULL
                AND m.deleted_at IS NULL
                AND b.deleted_at IS NULL
                AND mp.user_id = :userId
            """, nativeQuery = true)
    int countDueThisWeekForUser(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
                COUNT(DISTINCT mp.module_id) AS users_with_due_modules
            FROM
                spaced_learning.module_progress mp
            JOIN
            spaced_learning.repetitions r ON
                r.module_progress_id = mp.id
            JOIN
            spaced_learning.modules m ON
                mp.module_id = m.id
            JOIN
            spaced_learning.books b ON
                m.book_id = b.id
            JOIN
            spaced_learning.users u ON
                u.id = mp.user_id
            WHERE
                r.review_date <= DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day'
                AND mp.deleted_at IS NULL
                AND m.deleted_at IS NULL
                AND b.deleted_at IS NULL
                AND mp.user_id = :userId
            """, nativeQuery = true)
    int countDueThisMonthForUser(@Param("userId") UUID userId);

    /**
     * Count total words in modules due today for a user
     *
     * @param userId User ID
     * @return Total word count for modules due today
     */
    @Query(value = """
            SELECT
                COALESCE(SUM(m.word_count), 0) AS words_due_today
            FROM
                spaced_learning.module_progress mp
            JOIN
            spaced_learning.repetitions r ON
                r.module_progress_id = mp.id
            JOIN
            spaced_learning.modules m ON
                mp.module_id = m.id
            JOIN
            spaced_learning.books b ON
                m.book_id = b.id
            WHERE
                r.review_date = CURRENT_DATE
                AND mp.deleted_at IS NULL
                AND m.deleted_at IS NULL
                AND b.deleted_at IS NULL
                AND mp.user_id = :userId
            """, nativeQuery = true)
    int countWordsDueTodayForUser(@Param("userId") UUID userId);

    /**
     * Count total words in modules due this week for a user
     *
     * @param userId User ID
     * @return Total word count for modules due this week
     */
    @Query(value = """
            SELECT
            COALESCE(SUM(m.word_count), 0) AS words_due_in_week
            FROM
            spaced_learning.module_progress mp
            JOIN
            spaced_learning.modules m ON mp.module_id = m.id
            JOIN
            spaced_learning.books b ON m.book_id = b.id
            JOIN
            spaced_learning.repetitions r ON r.module_progress_id = mp.id
            WHERE
            r.review_date <= DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '6 days'
            AND mp.deleted_at IS NULL
            AND m.deleted_at IS NULL
            AND b.deleted_at IS NULL
            AND mp.user_id = :userId
            """, nativeQuery = true)
    int countWordsDueThisWeekForUser(@Param("userId") UUID userId);

    /**
     * Count total words in modules due this month for a user
     *
     * @param userId User ID
     * @return Total word count for modules due this month
     */
    @Query(value = """
            SELECT
            COALESCE(SUM(m.word_count), 0) AS words_due_today
            FROM
            spaced_learning.module_progress mp
            JOIN
            spaced_learning.modules m ON mp.module_id = m.id
            JOIN
            spaced_learning.books b ON m.book_id = b.id
            JOIN
            spaced_learning.repetitions r ON r.module_progress_id = mp.id
            WHERE
            r.review_date <= DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day'
            AND mp.deleted_at IS NULL
            AND m.deleted_at IS NULL
            AND b.deleted_at IS NULL
            AND mp.user_id = :userId
            """, nativeQuery = true)
    int countWordsDueThisMonthForUser(@Param("userId") UUID userId);

    /**
     * Count repetitions completed today for a user
     *
     * @param userId User ID
     * @return Number of repetitions completed today
     */
    @Query(value = """
            SELECT
                COUNT(DISTINCT mp.module_id) AS completed_modules_today
            FROM
                spaced_learning.module_progress mp
            JOIN
                spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN
                spaced_learning.modules m ON mp.module_id = m.id
            JOIN
                spaced_learning.books b ON m.book_id = b.id
            WHERE
                r.review_date = CURRENT_DATE
                AND r.status = 'COMPLETED'
                AND mp.deleted_at IS NULL
                AND m.deleted_at IS NULL
                AND b.deleted_at IS NULL
                AND mp.user_id = :userId
            """, nativeQuery = true)
    int countCompletedTodayForUser(@Param("userId") UUID userId);

    /**
     * Count total words completed today for a user
     *
     * @param userId User ID
     * @return Total word count for modules completed today
     */
    @Query(value = """
            SELECT
                COALESCE(SUM(m.word_count), 0) AS words_due_today
            FROM
                spaced_learning.module_progress mp
            JOIN
                spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN
                spaced_learning.modules m ON mp.module_id = m.id
            JOIN
                spaced_learning.books b ON m.book_id = b.id
            WHERE
                r.review_date = CURRENT_DATE
                AND r.status = 'COMPLETED'
                AND mp.deleted_at IS NULL
                AND m.deleted_at IS NULL
                AND b.deleted_at IS NULL
                AND mp.user_id = :userId
            """, nativeQuery = true)
    int countWordsCompletedTodayForUser(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
                COALESCE(SUM(m.word_count), 0) AS total_vocabulary_words
            FROM
                spaced_learning.modules m
            JOIN
                spaced_learning.books b ON
                m.book_id = b.id
            WHERE
                m.deleted_at IS NULL
                AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countTotalVocabularyWords();

    @Query(value = """
            SELECT
                COALESCE(SUM(m.word_count), 0) AS learned_vocabulary_words
            FROM
                spaced_learning.module_progress mp
            JOIN
                spaced_learning.modules m ON
                mp.module_id = m.id
            JOIN
                spaced_learning.books b ON
                m.book_id = b.id
            WHERE
                mp.first_learning_date IS NOT NULL
                AND mp.deleted_at IS NULL
                AND m.deleted_at IS NULL
                AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countLearnedVocabularyWords();
}