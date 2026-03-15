package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConnectionResponse(
    String id,
    @JsonProperty("user_id") String userId,
    String provider,
    String type,
    String status,
    @JsonProperty("redirect_url") String redirectUrl,
    @JsonProperty("last_synced_at") Instant lastSyncedAt,
    @JsonProperty("created_at") Instant createdAt,
    ConnectionErrorDto error
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ConnectionErrorDto(String code, String message) {}
}
