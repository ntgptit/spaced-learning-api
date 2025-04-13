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

        return rows.stream().map(row -> LearningModuleResponse.builder()
                .bookName(row[0] != null ? row[0].toString() : null)
                .bookNo(row[1] != null ? ((Number) row[1]).intValue() : null)
                .moduleTitle(row[2] != null ? row[2].toString() : null)
                .moduleNo(row[3] != null ? ((Number) row[3]).intValue() : null)
                .moduleWordCount(row[4] != null ? ((Number) row[4]).intValue() : null)
                .progressCyclesStudied(row[5] != null ? row[5].toString() : null)
                .progressNextStudyDate(
                        row[6] != null ? ((java.sql.Date) row[6]).toLocalDate() : null)
                .progressFirstLearningDate(
                        row[7] != null ? ((java.sql.Date) row[7]).toLocalDate() : null)
                .progressLatestPercentComplete(row[8] != null ? ((Number) row[8]).intValue() : null)
                .progressDueTaskCount(row[9] != null ? ((Number) row[9]).intValue() : 0)
                .moduleId(row[10] != null ? row[10].toString() : null)
                .studyHistory(
                        row[11] != null ? Arrays.asList(StringUtils.split(row[11].toString(), ", "))
                                : List.of())
                .build()).toList();

    }

}
