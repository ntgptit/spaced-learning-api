package com.spacedlearning.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Builder.Default
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleProgress> progress = new ArrayList<>();

    public ModuleProgress addProgress(ModuleProgress moduleProgress) {
        this.progress.add(moduleProgress);
        moduleProgress.setModule(this);
        return moduleProgress;
    }

    public boolean removeProgress(ModuleProgress moduleProgress) {
        final var removed = this.progress.remove(moduleProgress);
        if (removed) {
            moduleProgress.setModule(null);
        }
        return removed;
    }
}
