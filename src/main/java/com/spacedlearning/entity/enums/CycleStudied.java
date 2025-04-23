package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing the cycles studied status.
 */
@Getter
public enum CycleStudied {
    FIRST_TIME("FIRST_TIME"), FIRST_REVIEW("FIRST_REVIEW"), SECOND_REVIEW("SECOND_REVIEW"),
    THIRD_REVIEW("THIRD_REVIEW"), MORE_THAN_THREE_REVIEWS("MORE_THAN_THREE_REVIEWS");

    private final String value;

    CycleStudied(String value) {
        this.value = value;
    }

}