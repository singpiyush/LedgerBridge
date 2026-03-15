package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConnectionType {
    EMAIL("email"),
    BROKER("broker");

    private final String value;

    ConnectionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
