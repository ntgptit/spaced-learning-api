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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity for storing pre-calculated user statistics that are expensive to
 * compute on-the-fly
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_statistics", schema = "spaced_learning", indexes = {
        @Index(name = "idx_user_stats_user", columnList = "user_id") })
public class UserStatistics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Learning streak statistics
    @Column(name = "streak_days")
    private Integer streakDays = 0;

    @Column(name = "streak_weeks")
    private Integer streakWeeks = 0;

    @Column(name = "longest_streak_days")
    private Integer longestStreakDays = 0;

    // Timestamp of last statistics update
    @Column(name = "last_statistics_update")
    private LocalDateTime lastStatisticsUpdate;

    /**
     * Constructor with user
     *
     * @param user The user
     */
    public UserStatistics(User user) {
        this.user = user;
        lastStatisticsUpdate = LocalDateTime.now();
    }
}