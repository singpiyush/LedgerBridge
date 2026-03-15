package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AssetType {
    EQUITY("equity"),
    ETF("etf"),
    MUTUAL_FUND("mutual_fund"),
    BOND("bond"),
    COMMODITY("commodity");

    private final String value;

    AssetType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
