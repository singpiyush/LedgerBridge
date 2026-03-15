package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GainType {
    SHORT_TERM("short_term"),
    LONG_TERM("long_term");

    private final String value;

    GainType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
