package io.ledgerbridge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookEventType {
    INGESTION_COMPLETED("ingestion.completed"),
    INGESTION_FAILED("ingestion.failed"),
    INGESTION_REVIEW_REQUIRED("ingestion.review_required"),
    TRANSACTION_CREATED("transaction.created"),
    HOLDINGS_UPDATED("holdings.updated"),
    PORTFOLIO_RECOMPUTED("portfolio.recomputed"),
    CONNECTION_STATUS_CHANGED("connection.status_changed");

    private final String value;

    WebhookEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
