package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Token exchange request")
public record TokenRequest(
    @Schema(description = "The grant type for token exchange", example = "api_key",
            allowableValues = {"api_key", "authorization_code"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank @JsonProperty("grant_type") String grantType,

    @Schema(description = "Your API key (required when grant_type is api_key)", example = "lb_live_xxxxxxxxxxxxxx")
    @JsonProperty("api_key") String apiKey,

    @Schema(description = "Authorization code (required when grant_type is authorization_code)")
    String code,

    @Schema(description = "OAuth redirect URI (required when grant_type is authorization_code)", format = "uri")
    @JsonProperty("redirect_uri") String redirectUri
) {}
