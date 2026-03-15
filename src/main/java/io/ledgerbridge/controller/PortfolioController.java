package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.CapitalGainsResponse;
import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.PerformanceResponse;
import io.ledgerbridge.model.dto.PortfolioSummaryResponse;
import io.ledgerbridge.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/{user_id}/portfolio")
@Tag(name = "Portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Operation(
            summary = "Get portfolio summary",
            description = """
                    Returns an aggregated portfolio summary including total invested value,
                    current value, realized and unrealized gains, holdings count,
                    broker breakdown, and sector allocation.

                    **Note:** `current_value` requires market data integration. Without it,
                    the value reflects the invested amount.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio summary",
                    content = @Content(schema = @Schema(implementation = PortfolioSummaryResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PortfolioSummaryResponse> getPortfolio(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @PathVariable("user_id") String userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(userId));
    }

    @Operation(
            summary = "Get portfolio performance over time",
            description = """
                    Returns time-series data showing portfolio value over the specified period.
                    Optionally compare against a benchmark index (e.g., NIFTY50).

                    Includes summary metrics: total return, benchmark return, alpha, and max drawdown.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Performance data",
                    content = @Content(schema = @Schema(implementation = PerformanceResponse.class)))
    })
    @GetMapping("/performance")
    public ResponseEntity<PerformanceResponse> getPerformance(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @PathVariable("user_id") String userId,
            @Parameter(description = "Time period", required = true,
                    schema = @Schema(allowableValues = {"1d", "1w", "1m", "3m", "6m", "1y", "ytd", "all"}))
            @RequestParam("period") String period,
            @Parameter(description = "Benchmark index for comparison", example = "NIFTY50")
            @RequestParam(value = "benchmark", required = false) String benchmark) {
        return ResponseEntity.ok(portfolioService.getPerformance(userId, period, benchmark));
    }

    @Operation(
            summary = "Get realized and unrealized capital gains",
            description = """
                    Returns a tax-ready breakdown of short-term and long-term capital gains
                    for a given Indian financial year (April to March).

                    Includes per-transaction gain/loss details and estimated tax liability.

                    **Classification:** Equity held > 12 months = long-term, otherwise short-term.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Capital gains report",
                    content = @Content(schema = @Schema(implementation = CapitalGainsResponse.class)))
    })
    @GetMapping("/gains")
    public ResponseEntity<CapitalGainsResponse> getCapitalGains(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @PathVariable("user_id") String userId,
            @Parameter(description = "Indian financial year (e.g., 2025-26)", example = "2025-26", required = true)
            @RequestParam("financial_year") String financialYear) {
        return ResponseEntity.ok(portfolioService.getCapitalGains(userId, financialYear));
    }
}
