package com.spacedlearning.repository.custom.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.repository.custom.LearningModuleRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class LearningModuleRepositoryImpl implements LearningModuleRepository {

    @PersistenceContext
    private EntityManager entityManager;

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

    @Override
    public List<LearningModuleResponse> findModuleStudyProgress(int offset, int limit) {

        final String sql = """
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
                FROM
                    modules m
                INNER JOIN books b ON
                    b.id = m.book_id
                LEFT JOIN module_progress mp ON
                    mp.module_id = m.id AND mp.deleted_at IS NULL
                LEFT JOIN (
                    SELECT
                        next_study_date::date AS study_date,
                        COUNT(*) AS modules_on_same_day
                    FROM
                        module_progress
                    WHERE
                        next_study_date IS NOT NULL
                        AND deleted_at IS NULL
                    GROUP BY
                        next_study_date::date
                ) msd ON msd.study_date = mp.next_study_date::date
                LEFT JOIN repetitions r ON
                    r.module_progress_id = mp.id
                    AND r.status = 'COMPLETED'
                    AND r.review_date IS NOT NULL
                WHERE
                    b.deleted_at IS NULL
                    AND m.deleted_at IS NULL
                GROUP BY
                    b."name",
                    b.book_no,
                    m.title,
                    m.module_no,
                    m.word_count,
                    mp.cycles_studied,
                    mp.next_study_date,
                    mp.first_learning_date,
                    mp.percent_complete,
                    msd.modules_on_same_day,
                    m.id
                ORDER BY
                    mp.next_study_date NULLS LAST,
                    mp.percent_complete,
                    m.word_count,
                    b.book_no DESC,
                    m.module_no;
                """;

        final Query query = entityManager.createNativeQuery(sql);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Object[]> rows = query.getResultList();

        return rows.stream().map(row -> LearningModuleResponse
                .builder()
                .bookName(row[IDX_BOOK_NAME] != null ? row[IDX_BOOK_NAME].toString() : null)
                .bookNo(row[IDX_BOOK_NO] != null ? ((Number) row[IDX_BOOK_NO]).intValue() : null)
                .moduleTitle(row[IDX_MODULE_TITLE] != null ? row[IDX_MODULE_TITLE].toString() : null)
                .moduleNo(row[IDX_MODULE_NO] != null ? ((Number) row[IDX_MODULE_NO]).intValue() : null)
                .moduleWordCount(row[IDX_MODULE_WORD_COUNT] != null ? ((Number) row[IDX_MODULE_WORD_COUNT]).intValue()
                        : null)
                .progressCyclesStudied(row[IDX_CYCLES_STUDIED] != null ? row[IDX_CYCLES_STUDIED].toString() : null)
                .progressNextStudyDate(
                        row[IDX_NEXT_STUDY_DATE] != null ? ((java.sql.Date) row[IDX_NEXT_STUDY_DATE]).toLocalDate()
                                : null)
                .progressFirstLearningDate(
                        row[IDX_FIRST_LEARNING_DATE] != null ? ((java.sql.Date) row[IDX_FIRST_LEARNING_DATE])
                                .toLocalDate() : null)
                .progressLatestPercentComplete(row[IDX_PERCENT_COMPLETE] != null ? ((Number) row[IDX_PERCENT_COMPLETE])
                        .intValue() : null)
                .progressDueTaskCount(row[IDX_DUE_TASK_COUNT] != null ? ((Number) row[IDX_DUE_TASK_COUNT]).intValue()
                        : 0)
                .moduleId(row[IDX_MODULE_ID] != null ? row[IDX_MODULE_ID].toString() : null)
                .studyHistory(
                        row[IDX_REVIEW_DATES] != null ? Arrays
                                .asList(StringUtils.split(row[IDX_REVIEW_DATES].toString(), ", "))
                                : List.of())
                .build()).toList();

    }

}
