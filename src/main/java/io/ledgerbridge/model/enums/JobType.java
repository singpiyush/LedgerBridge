package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobType {
    EMAIL_SCAN("email_scan"),
    DOCUMENT_PARSE("document_parse"),
    PORTFOLIO_RECOMPUTE("portfolio_recompute");

    private final String value;

    JobType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
