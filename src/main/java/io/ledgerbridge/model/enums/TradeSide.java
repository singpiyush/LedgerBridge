package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TradeSide {
    BUY("buy"),
    SELL("sell");

    private final String value;

    TradeSide(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
