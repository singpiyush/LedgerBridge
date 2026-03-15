package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "A portfolio holding representing a position in an asset")
public record HoldingResponse(
    @Schema(description = "Unique holding ID", example = "hld_p6q7r8")
    String id,

    @Schema(description = "User who owns this holding", example = "usr_k8m2n4")
    @JsonProperty("user_id") String userId,

    @Schema(description = "The held asset")
    AssetRef asset,

    @Schema(description = "Current quantity held", example = "15")
    BigDecimal quantity,

    @Schema(description = "Weighted average cost basis per unit", example = "3350.00")
    @JsonProperty("avg_price") BigDecimal avgPrice,

    @Schema(description = "Total invested value (quantity x avg_price)", example = "50250.00")
    @JsonProperty("invested_value") BigDecimal investedValue,

    @Schema(description = "Broker where this position is held", example = "zerodha")
    String broker,

    @Schema(description = "Exchange", example = "NSE")
    String exchange,

    @Schema(description = "Date of first purchase", example = "2025-06-15")
    @JsonProperty("first_bought_at") LocalDate firstBoughtAt,

    @Schema(description = "When this holding was last recalculated")
    @JsonProperty("last_updated") Instant lastUpdated
) {}
