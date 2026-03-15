package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WebhookResponse(
    String id,
    String url,
    List<String> events,
    String secret,
    boolean enabled,
    @JsonProperty("created_at") Instant createdAt
) {}
