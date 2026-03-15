package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Access token response")
public record TokenResponse(
    @Schema(description = "The JWT access token", example = "eyJhbGciOiJIUzI1NiIs...")
    @JsonProperty("access_token") String accessToken,

    @Schema(description = "Token type (always Bearer)", example = "Bearer")
    @JsonProperty("token_type") String tokenType,

    @Schema(description = "Token lifetime in seconds", example = "3600")
    @JsonProperty("expires_in") int expiresIn,

    @Schema(description = "Refresh token for obtaining new access tokens")
    @JsonProperty("refresh_token") String refreshToken,

    @Schema(description = "Granted scopes", example = "read write")
    String scope
) {}
