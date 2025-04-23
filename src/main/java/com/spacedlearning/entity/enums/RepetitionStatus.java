package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing the status of repetition.
 */
@Getter
public enum RepetitionStatus {
    NOT_STARTED("NOT_STARTED"), COMPLETED("COMPLETED"), SKIPPED("SKIPPED");

    private final String value;

    RepetitionStatus(String value) {
        this.value = value;
    }

}