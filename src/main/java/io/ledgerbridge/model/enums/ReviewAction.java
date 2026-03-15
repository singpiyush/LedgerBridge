package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReviewAction {
    ACCEPT("accept"),
    REJECT("reject"),
    CORRECT("correct");

    private final String value;

    ReviewAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
