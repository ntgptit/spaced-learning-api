// Grammar.java
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
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "usage_note", columnDefinition = "TEXT")
    private String usageNote;

    @Column(name = "example", columnDefinition = "TEXT")
    private String example;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}