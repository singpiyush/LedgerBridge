package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.CapitalGainsResponse;
import io.ledgerbridge.model.dto.PerformanceResponse;
import io.ledgerbridge.model.dto.PortfolioSummaryResponse;
import io.ledgerbridge.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/{user_id}/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<PortfolioSummaryResponse> getPortfolio(
            @PathVariable("user_id") String userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary(userId));
    }

    @GetMapping("/performance")
    public ResponseEntity<PerformanceResponse> getPerformance(
            @PathVariable("user_id") String userId,
            @RequestParam("period") String period,
            @RequestParam(value = "benchmark", required = false) String benchmark) {
        return ResponseEntity.ok(portfolioService.getPerformance(userId, period, benchmark));
    }

    @GetMapping("/gains")
    public ResponseEntity<CapitalGainsResponse> getCapitalGains(
            @PathVariable("user_id") String userId,
            @RequestParam("financial_year") String financialYear) {
        return ResponseEntity.ok(portfolioService.getCapitalGains(userId, financialYear));
    }
}
