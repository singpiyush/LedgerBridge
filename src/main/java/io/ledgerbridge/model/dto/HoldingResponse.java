package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record HoldingResponse(
    String id,
    @JsonProperty("user_id") String userId,
    AssetRef asset,
    BigDecimal quantity,
    @JsonProperty("avg_price") BigDecimal avgPrice,
    @JsonProperty("invested_value") BigDecimal investedValue,
    String broker,
    String exchange,
    @JsonProperty("first_bought_at") LocalDate firstBoughtAt,
    @JsonProperty("last_updated") Instant lastUpdated
) {}
