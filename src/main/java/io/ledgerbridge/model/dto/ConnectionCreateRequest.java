package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create a new email or broker connection")
public record ConnectionCreateRequest(
    @Schema(description = "The user to link this connection to", example = "usr_k8m2n4",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank @JsonProperty("user_id") String userId,

    @Schema(description = "The email or broker provider", example = "gmail",
            allowableValues = {"gmail", "outlook", "zerodha", "upstox", "groww"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull String provider,

    @Schema(description = "Connection type", example = "email",
            allowableValues = {"email", "broker"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull String type,

    @Schema(description = "OAuth callback URL (required for email connections)", example = "https://yourapp.com/callback",
            format = "uri")
    @JsonProperty("redirect_uri") String redirectUri,

    @Schema(description = "Broker credentials (required for broker connections)")
    Credentials credentials
) {
    @Schema(description = "Broker-specific credentials")
    public record Credentials(
        @Schema(description = "Broker client/user ID", example = "AB1234")
        @JsonProperty("client_id") String clientId,

        @Schema(description = "Broker API secret")
        @JsonProperty("api_secret") String apiSecret
    ) {}
}
