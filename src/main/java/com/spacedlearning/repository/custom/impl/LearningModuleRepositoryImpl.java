package com.spacedlearning.repository.custom.impl;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.repository.custom.LearningModuleRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class LearningModuleRepositoryImpl implements LearningModuleRepository {

    private static final int IDX_BOOK_NAME = 0;

    private static final int IDX_BOOK_NO = 1;
    private static final int IDX_MODULE_TITLE = 2;
    private static final int IDX_MODULE_NO = 3;
    private static final int IDX_MODULE_WORD_COUNT = 4;
    private static final int IDX_CYCLES_STUDIED = 5;
    private static final int IDX_NEXT_STUDY_DATE = 6;
    private static final int IDX_FIRST_LEARNING_DATE = 7;
    private static final int IDX_PERCENT_COMPLETE = 8;
    private static final int IDX_DUE_TASK_COUNT = 9;
    private static final int IDX_MODULE_ID = 10;
    private static final int IDX_REVIEW_DATES = 11;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<LearningModuleResponse> findModuleStudyProgress(int offset, int limit) {
        final var sql = """
                SELECT
                    b."name" AS book_name,
                    b.book_no AS book_no,
                    m.title AS module_title,
                    m.module_no AS module_no,
                    m.word_count AS module_word_count,
                    mp.cycles_studied AS progress_cycles_studied,
                    mp.next_study_date AS progress_next_study_date,
                    mp.first_learning_date AS progress_first_learning_date,
                    mp.percent_complete AS progress_latest_percent_complete,
                    msd.modules_on_same_day AS progress_due_task_count,
                    m.id AS module_id,
                    STRING_AGG(TO_CHAR(r.review_date, 'YYYY-MM-DD'), ', ' ORDER BY r.review_date DESC) AS review_dates
                FROM spaced_learning.modules m
                INNER JOIN spaced_learning.books b ON b.id = m.book_id
                LEFT JOIN spaced_learning.module_progress mp ON mp.module_id = m.id AND mp.deleted_at IS NULL
                LEFT JOIN (
                    SELECT
                        next_study_date::date AS study_date,
                        COUNT(*) AS modules_on_same_day
                    FROM spaced_learning.module_progress
                    WHERE next_study_date IS NOT NULL AND deleted_at IS NULL
                    GROUP BY next_study_date::date
                ) msd ON msd.study_date = mp.next_study_date::date
                LEFT JOIN spaced_learning.repetitions r ON r.module_progress_id = mp.id
                    AND r.status = 'COMPLETED' AND r.review_date IS NOT NULL
                WHERE b.deleted_at IS NULL AND m.deleted_at IS NULL
                GROUP BY
                    b."name", b.book_no, m.title, m.module_no, m.word_count,
                    mp.cycles_studied, mp.next_study_date, mp.first_learning_date,
                    mp.percent_complete, msd.modules_on_same_day, m.id
                ORDER BY
                    mp.next_study_date NULLS LAST,
                    mp.percent_complete,
                    m.word_count,
                    b.book_no DESC,
                    m.module_no
                """;

        final var query = this.entityManager.createNativeQuery(sql);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Object[]> rows = query.getResultList();

        return rows.stream()
                .map(this::mapRowToResponse)
                .toList();
    }

    private LearningModuleResponse mapRowToResponse(Object[] row) {
        return LearningModuleResponse.builder()
                .bookName(toString(row[IDX_BOOK_NAME]))
                .bookNo(toInt(row[IDX_BOOK_NO]))
                .moduleTitle(toString(row[IDX_MODULE_TITLE]))
                .moduleNo(toInt(row[IDX_MODULE_NO]))
                .moduleWordCount(toInt(row[IDX_MODULE_WORD_COUNT]))
                .progressCyclesStudied(toString(row[IDX_CYCLES_STUDIED]))
                .progressNextStudyDate(toLocalDate(row[IDX_NEXT_STUDY_DATE]))
                .progressFirstLearningDate(toLocalDate(row[IDX_FIRST_LEARNING_DATE]))
                .progressLatestPercentComplete(toInt(row[IDX_PERCENT_COMPLETE]))
                .progressDueTaskCount(toInt(row[IDX_DUE_TASK_COUNT], 0))
                .moduleId(toUUIDString(row[IDX_MODULE_ID]))
                .studyHistory(parseReviewDates(row[IDX_REVIEW_DATES]))
                .build();
    }

    private List<String> parseReviewDates(Object obj) {
        if (obj == null) {
            return List.of();
        }
        return Arrays.asList(StringUtils.split(obj.toString(), ", "));
    }

    private Integer toInt(Object obj) {
        return obj != null ? ((Number) obj).intValue() : null;
    }

    private Integer toInt(Object obj, int defaultValue) {
        return obj != null ? ((Number) obj).intValue() : defaultValue;
    }

    private java.time.LocalDate toLocalDate(Object obj) {
        return obj instanceof final Date date ? date.toLocalDate() : null;
    }

    private String toString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private String toUUIDString(Object obj) {
        return obj instanceof final UUID uuid ? uuid.toString() : Objects.toString(obj, null);
    }
}
