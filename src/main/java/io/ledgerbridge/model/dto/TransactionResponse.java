package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
    String id,
    @JsonProperty("user_id") String userId,
    AssetRef asset,
    BigDecimal quantity,
    BigDecimal price,
    @JsonProperty("total_value") BigDecimal totalValue,
    String side,
    LocalDate date,
    String broker,
    String exchange,
    @JsonProperty("ingestion_id") String ingestionId,
    ChargesDto charges,
    @JsonProperty("created_at") Instant createdAt
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChargesDto(
        BigDecimal brokerage,
        BigDecimal stt,
        BigDecimal gst,
        @JsonProperty("stamp_duty") BigDecimal stampDuty,
        BigDecimal total
    ) {}
}
