package com.mohit_sunda.virtual_card_platform.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CardStatus {
    ACTIVE("Active"),
    BLOCKED("Blocked");

    private final String displayName;

    CardStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
