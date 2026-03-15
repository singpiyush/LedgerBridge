package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConnectionStatus {
    PENDING("pending"),
    ACTIVE("active"),
    EXPIRED("expired"),
    REVOKED("revoked"),
    ERROR("error");

    private final String value;

    ConnectionStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
