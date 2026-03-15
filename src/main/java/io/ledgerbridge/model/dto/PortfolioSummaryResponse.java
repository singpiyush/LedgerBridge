package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PortfolioSummaryResponse(
    @JsonProperty("user_id") String userId,
    @JsonProperty("total_invested") BigDecimal totalInvested,
    @JsonProperty("current_value") BigDecimal currentValue,
    @JsonProperty("total_gain") BigDecimal totalGain,
    @JsonProperty("total_gain_pct") BigDecimal totalGainPct,
    @JsonProperty("realized_gain") BigDecimal realizedGain,
    @JsonProperty("unrealized_gain") BigDecimal unrealizedGain,
    @JsonProperty("holdings_count") int holdingsCount,
    List<String> brokers,
    @JsonProperty("sector_allocation") List<AllocationDto> sectorAllocation,
    @JsonProperty("as_of") Instant asOf
) {
    public record AllocationDto(
        String name,
        BigDecimal value,
        BigDecimal percentage
    ) {}
}
