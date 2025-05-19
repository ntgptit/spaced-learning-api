package com.spacedlearning.dto.module;

import com.spacedlearning.dto.grammar.GrammarResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.vocabulary.VocabularyResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for detailed module response including progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDetailResponse {
    private UUID id;
    private UUID bookId;
    private String bookName;
    private Integer moduleNo;
    private String title;
    private Integer wordCount;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ModuleProgressSummaryResponse> progress;

    private List<VocabularyResponse> vocabularies;
    private List<GrammarResponse> grammars;

    private Integer vocabularyCount;
    private Integer grammarCount;
}