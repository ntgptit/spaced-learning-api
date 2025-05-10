package com.spacedlearning.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_statistics", schema = "spaced_learning", indexes = @Index(name = "idx_user_stats_user", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "user")
public class UserStatistics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Learning streak statistics
    @Builder.Default
    @Column(name = "streak_days")
    private Integer streakDays = 0;

    @Builder.Default
    @Column(name = "streak_weeks")
    private Integer streakWeeks = 0;

    @Builder.Default
    @Column(name = "longest_streak_days")
    private Integer longestStreakDays = 0;

    // Timestamp of last statistics update
    @Column(name = "last_statistics_update")
    private LocalDateTime lastStatisticsUpdate;

    /**
     * Constructor with user
     */
    public UserStatistics(User user) {
        this.user = user;
        this.streakDays = 0;
        this.streakWeeks = 0;
        this.longestStreakDays = 0;
        this.lastStatisticsUpdate = LocalDateTime.now();
    }
}
