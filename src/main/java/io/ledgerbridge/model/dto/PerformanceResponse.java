package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PerformanceResponse(
    String period,
    @JsonProperty("data_points") List<DataPoint> dataPoints,
    Summary summary
) {
    public record DataPoint(
        LocalDate date,
        @JsonProperty("portfolio_value") BigDecimal portfolioValue,
        @JsonProperty("benchmark_value") BigDecimal benchmarkValue,
        @JsonProperty("daily_return_pct") BigDecimal dailyReturnPct
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Summary(
        @JsonProperty("total_return_pct") BigDecimal totalReturnPct,
        @JsonProperty("benchmark_return_pct") BigDecimal benchmarkReturnPct,
        BigDecimal alpha,
        @JsonProperty("max_drawdown_pct") BigDecimal maxDrawdownPct
    ) {}
}
