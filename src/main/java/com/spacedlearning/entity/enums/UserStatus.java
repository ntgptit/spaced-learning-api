package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible user statuses.
 */
@Getter
public enum UserStatus {
    ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), SUSPENDED("SUSPENDED");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

}