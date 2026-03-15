package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
    @NotBlank @JsonProperty("grant_type") String grantType,
    @JsonProperty("api_key") String apiKey,
    String code,
    @JsonProperty("redirect_uri") String redirectUri
) {}
