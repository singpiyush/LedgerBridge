package io.ledgerbridge.model.dto;

import java.util.List;

public record WebhookUpdateRequest(
    String url,
    List<String> events,
    Boolean enabled
) {}
