// VocabularyRepository.java
package com.spacedlearning.repository;

import com.spacedlearning.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID> {

    List<Vocabulary> findByModuleIdOrderByTermAsc(UUID moduleId);

    Page<Vocabulary> findByModuleId(UUID moduleId, Pageable pageable);

    @Query("SELECT v FROM Vocabulary v WHERE LOWER(v.term) LIKE LOWER(CONCAT('%', :term, '%')) AND v.deletedAt IS NULL")
    Page<Vocabulary> searchByTerm(@Param("term") String term, Pageable pageable);

    @Query("SELECT COUNT(v) FROM Vocabulary v WHERE v.module.id = :moduleId AND v.deletedAt IS NULL")
    int countByModuleId(@Param("moduleId") UUID moduleId);
}