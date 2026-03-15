package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "A normalized financial transaction")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
    @Schema(description = "Unique transaction ID", example = "txn_m3n4o5")
    String id,

    @Schema(description = "User who owns this transaction", example = "usr_k8m2n4")
    @JsonProperty("user_id") String userId,

    @Schema(description = "The traded asset")
    AssetRef asset,

    @Schema(description = "Number of units traded", example = "5")
    BigDecimal quantity,

    @Schema(description = "Price per unit", example = "3400.00")
    BigDecimal price,

    @Schema(description = "Total transaction value (quantity x price)", example = "17000.00")
    @JsonProperty("total_value") BigDecimal totalValue,

    @Schema(description = "Trade direction", example = "buy", allowableValues = {"buy", "sell"})
    String side,

    @Schema(description = "Trade date", example = "2026-03-15")
    LocalDate date,

    @Schema(description = "Broker that executed the trade", example = "zerodha")
    String broker,

    @Schema(description = "Exchange where the trade was executed", example = "NSE")
    String exchange,

    @Schema(description = "The ingestion that produced this transaction", example = "ing_a1b2c3d4")
    @JsonProperty("ingestion_id") String ingestionId,

    @Schema(description = "Breakdown of transaction charges")
    ChargesDto charges,

    @Schema(description = "When this transaction was recorded")
    @JsonProperty("created_at") Instant createdAt
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Transaction charges breakdown (brokerage, taxes, duties)")
    public record ChargesDto(
        @Schema(description = "Brokerage fee", example = "20.00")
        BigDecimal brokerage,

        @Schema(description = "Securities Transaction Tax", example = "17.00")
        BigDecimal stt,

        @Schema(description = "GST on brokerage", example = "3.60")
        BigDecimal gst,

        @Schema(description = "Stamp duty", example = "2.55")
        @JsonProperty("stamp_duty") BigDecimal stampDuty,

        @Schema(description = "Total charges", example = "43.15")
        BigDecimal total
    ) {}
}
