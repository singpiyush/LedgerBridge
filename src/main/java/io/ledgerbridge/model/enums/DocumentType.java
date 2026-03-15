package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    CONTRACT_NOTE("contract_note"),
    HOLDINGS_STATEMENT("holdings_statement"),
    TRANSACTION_HISTORY("transaction_history"),
    OTHER("other");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
