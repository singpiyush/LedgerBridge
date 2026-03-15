package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConnectionProvider {
    GMAIL("gmail"),
    OUTLOOK("outlook"),
    ZERODHA("zerodha"),
    UPSTOX("upstox"),
    GROWW("groww");

    private final String value;

    ConnectionProvider(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
