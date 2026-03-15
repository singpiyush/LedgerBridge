package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IngestionSource {
    EMAIL("email"),
    UPLOAD("upload");

    private final String value;

    IngestionSource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
