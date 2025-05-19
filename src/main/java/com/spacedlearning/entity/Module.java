package com.spacedlearning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(exclude = "progress")
public class Module extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull
    @Min(1)
    @Column(name = "module_no", nullable = false)
    private Integer moduleNo;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Builder.Default
    @Min(0)
    @Column(name = "word_count", nullable = false)
    private Integer wordCount = 0;

    @URL(message = "The URL must be a valid URL format")
    @Column(name = "url", length = 500)
    private String url;

    @Builder.Default
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleProgress> progress = new ArrayList<>();

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vocabulary> vocabularies = new ArrayList<>();
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Grammar> grammars = new ArrayList<>();

}