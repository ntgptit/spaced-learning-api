// Vocabulary.java
package com.spacedlearning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "vocabularies", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "module")
public class Vocabulary extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "term", length = 100, nullable = false)
    private String term;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "example", columnDefinition = "TEXT")
    private String example;

    @Column(name = "pronunciation", length = 100)
    private String pronunciation;

    @NotBlank
    @Size(max = 100)
    @Column(name = "part_of_speech", length = 100)
    private String partOfSpeech;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}