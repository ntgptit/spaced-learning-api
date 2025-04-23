package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible difficulty levels.
 */
@Getter
public enum DifficultyLevel {
    BEGINNER("BEGINNER"), INTERMEDIATE("INTERMEDIATE"), ADVANCED("ADVANCED"), EXPERT("EXPERT");

    private final String value;

    DifficultyLevel(String value) {
        this.value = value;
    }

}