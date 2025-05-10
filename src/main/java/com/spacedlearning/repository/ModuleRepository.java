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

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {

    // Statistic: Modules completed today
    @Query(value = """
            SELECT COUNT(DISTINCT mp.module_id)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE r.review_date = CURRENT_DATE
              AND r.status = 'COMPLETED'
              AND mp.deleted_at IS NULL
              AND m.deleted_at IS NULL
              AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countCompletedToday();

    // Statistic: Modules due this month
    @Query(value = """
            SELECT COUNT(DISTINCT mp.module_id)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE r.review_date <= DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day'
              AND mp.deleted_at IS NULL
              AND m.deleted_at IS NULL
              AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countDueThisMonth();

    // Statistic: Modules due this week
    @Query(value = """
            SELECT COUNT(DISTINCT mp.module_id)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE r.review_date <= DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '6 days'
              AND mp.deleted_at IS NULL
              AND m.deleted_at IS NULL
              AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countDueThisWeek();

    // Statistic: Modules due today
    @Query(value = """
            SELECT COUNT(DISTINCT mp.module_id)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE r.review_date <= CURRENT_DATE
              AND mp.deleted_at IS NULL
              AND m.deleted_at IS NULL
              AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countDueToday();

    // Statistic: Learned word count
    @Query(value = """
            SELECT COALESCE(SUM(m.word_count), 0)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE mp.first_learning_date IS NOT NULL
              AND mp.deleted_at IS NULL
              AND m.deleted_at IS NULL
              AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countLearnedVocabularyWords();

    @Query(value = """
            SELECT COUNT(*)
            FROM spaced_learning.modules m
            LEFT JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE m.deleted_at IS NULL AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countTotalModules();

    @Query(value = """
            SELECT COALESCE(SUM(m.word_count), 0)
            FROM spaced_learning.modules m
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE m.deleted_at IS NULL AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countTotalVocabularyWords();

    @Query(value = """
            SELECT COALESCE(SUM(m.word_count), 0)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE r.review_date = CURRENT_DATE AND r.status = 'COMPLETED'
              AND mp.deleted_at IS NULL AND m.deleted_at IS NULL AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countWordsCompletedToday();

    @Query(value = """
            SELECT COALESCE(SUM(m.word_count), 0)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            WHERE r.review_date <= DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day'
              AND mp.deleted_at IS NULL AND m.deleted_at IS NULL AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countWordsDueThisMonth();

    @Query(value = """
            SELECT COALESCE(SUM(m.word_count), 0)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            WHERE r.review_date <= DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '6 days'
              AND mp.deleted_at IS NULL AND m.deleted_at IS NULL AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countWordsDueThisWeek();

    @Query(value = """
            SELECT COALESCE(SUM(m.word_count), 0)
            FROM spaced_learning.module_progress mp
            JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
            JOIN spaced_learning.modules m ON mp.module_id = m.id
            JOIN spaced_learning.books b ON m.book_id = b.id
            WHERE r.review_date = CURRENT_DATE
              AND mp.deleted_at IS NULL AND m.deleted_at IS NULL AND b.deleted_at IS NULL
            """, nativeQuery = true)
    int countWordsDueToday();

    // Business logic queries
    boolean existsByBookIdAndModuleNo(UUID bookId, Integer moduleNo);

    Page<Module> findByBookId(UUID bookId, Pageable pageable);

    List<Module> findByBookIdOrderByModuleNo(UUID bookId);

    @Query("SELECT COALESCE(MAX(m.moduleNo), 0) FROM Module m WHERE m.book.id = :bookId")
    Integer findMaxModuleNoByBookId(@Param("bookId") UUID bookId);

    @EntityGraph(attributePaths = { "progress" })
    Optional<Module> findWithProgressById(UUID id);

    @Query(value = """
            SELECT COALESCE(SUM(
                CASE
                    WHEN mp.percent_complete = 100 THEN m.word_count
                    ELSE CAST((m.word_count * mp.percent_complete / 100) AS INT)
                END
            ), 0)
            FROM spaced_learning.modules m
            JOIN spaced_learning.module_progress mp ON m.id = mp.module_id
            WHERE m.deleted_at IS NULL AND mp.deleted_at IS NULL
            """, nativeQuery = true)
    int getLearnedWordCount();

    @Query(value = """
            WITH cycle_types AS (
                SELECT 'FIRST_TIME' AS cycle_name
                UNION ALL SELECT 'FIRST_REVIEW'
                UNION ALL SELECT 'SECOND_REVIEW'
                UNION ALL SELECT 'THIRD_REVIEW'
                UNION ALL SELECT 'MORE_THAN_THREE_REVIEWS'
            )
            SELECT ct.cycle_name, COALESCE(COUNT(mp.cycles_studied), 0)
            FROM cycle_types ct
            LEFT JOIN (
                spaced_learning.module_progress mp
                INNER JOIN spaced_learning.modules m ON m.id = mp.module_id
                INNER JOIN spaced_learning.books b ON m.book_id = b.id
                WHERE b.deleted_at IS NULL AND m.deleted_at IS NULL
            ) ON ct.cycle_name = mp.cycles_studied
            GROUP BY ct.cycle_name
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
            SELECT COALESCE(SUM(m.word_count), 0)
            FROM spaced_learning.modules m
            JOIN spaced_learning.module_progress mp ON m.id = mp.module_id
            WHERE m.deleted_at IS NULL AND mp.deleted_at IS NULL
            """, nativeQuery = true)
    int getTotalWordCount();
}
