package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.HoldingResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.service.HoldingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/{user_id}/holdings")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<HoldingResponse>> listHoldings(
            @PathVariable("user_id") String userId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "25") int limit,
            @RequestParam(value = "broker", required = false) String broker,
            @RequestParam(value = "symbol", required = false) String symbol) {
        return ResponseEntity.ok(holdingService.listHoldings(
                userId, cursor, Math.min(limit, 100), broker, symbol));
    }

    @GetMapping("/{holding_id}")
    public ResponseEntity<HoldingResponse> getHolding(
            @PathVariable("user_id") String userId,
            @PathVariable("holding_id") String holdingId) {
        return ResponseEntity.ok(holdingService.getHolding(userId, holdingId));
    }
}
