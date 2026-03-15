package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CapitalGainsResponse(
    @JsonProperty("financial_year") String financialYear,
    @JsonProperty("short_term") GainBreakdown shortTerm,
    @JsonProperty("long_term") GainBreakdown longTerm,
    @JsonProperty("total_realized") BigDecimal totalRealized,
    List<GainTransaction> transactions
) {
    public record GainBreakdown(
        BigDecimal gain,
        BigDecimal loss,
        BigDecimal net,
        @JsonProperty("tax_liability_estimate") BigDecimal taxLiabilityEstimate
    ) {}

    public record GainTransaction(
        AssetRef asset,
        @JsonProperty("buy_date") LocalDate buyDate,
        @JsonProperty("sell_date") LocalDate sellDate,
        @JsonProperty("buy_price") BigDecimal buyPrice,
        @JsonProperty("sell_price") BigDecimal sellPrice,
        BigDecimal quantity,
        BigDecimal gain,
        String type
    ) {}
}
