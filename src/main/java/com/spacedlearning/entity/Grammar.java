package com.spacedlearning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "grammars", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "module")
public class Grammar extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "grammar_pattern", length = 100, nullable = false)
    private String grammarPattern;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "structure", columnDefinition = "TEXT")
    private String structure;

    @Column(name = "conjugation", columnDefinition = "TEXT")
    private String conjugation;

    @Column(name = "examples", columnDefinition = "TEXT")
    private String examples;

    @Column(name = "common_phrases", columnDefinition = "TEXT")
    private String commonPhrases;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}