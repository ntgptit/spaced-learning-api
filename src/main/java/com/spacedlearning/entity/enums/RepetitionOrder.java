package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing the order of repetition.
 */
@Getter
public enum RepetitionOrder {
    FIRST_REPETITION("1st repetition"), SECOND_REPETITION("2nd repetition"), THIRD_REPETITION("3rd repetition"),
    FOURTH_REPETITION("4th repetition"), FIFTH_REPETITION("5th repetition");

    private final String value;

    RepetitionOrder(String value) {
        this.value = value;
    }

}