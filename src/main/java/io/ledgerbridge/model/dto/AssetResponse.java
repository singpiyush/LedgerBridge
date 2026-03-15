package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AssetResponse(
    String id,
    String symbol,
    String name,
    String exchange,
    String isin,
    String sector,
    String industry,
    String currency,
    String country,
    @JsonProperty("asset_type") String assetType
) {}
