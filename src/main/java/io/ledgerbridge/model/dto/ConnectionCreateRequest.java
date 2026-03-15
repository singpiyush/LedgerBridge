package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConnectionCreateRequest(
    @NotBlank @JsonProperty("user_id") String userId,
    @NotNull String provider,
    @NotNull String type,
    @JsonProperty("redirect_uri") String redirectUri,
    Credentials credentials
) {
    public record Credentials(
        @JsonProperty("client_id") String clientId,
        @JsonProperty("api_secret") String apiSecret
    ) {}
}
