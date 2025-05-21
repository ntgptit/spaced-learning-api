// GrammarRepository.java
package com.spacedlearning.repository;

import com.spacedlearning.entity.Grammar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrammarRepository extends JpaRepository<Grammar, UUID> {

    List<Grammar> findByModuleIdOrderByGrammarPatternAsc(UUID moduleId);

    Page<Grammar> findByModuleId(UUID moduleId, Pageable pageable);

    @Query("SELECT g FROM Grammar g WHERE LOWER(g.grammarPattern) LIKE LOWER(CONCAT('%', :pattern, '%')) AND g.deletedAt IS NULL")
    Page<Grammar> searchByGrammarPattern(@Param("pattern") String pattern, Pageable pageable);

    @Query("SELECT COUNT(g) FROM Grammar g WHERE g.module.id = :moduleId AND g.deletedAt IS NULL")
    int countByModuleId(@Param("moduleId") UUID moduleId);
}