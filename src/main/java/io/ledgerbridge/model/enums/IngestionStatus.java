package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IngestionStatus {
    QUEUED("queued"),
    PROCESSING("processing"),
    REVIEW_REQUIRED("review_required"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    IngestionStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
