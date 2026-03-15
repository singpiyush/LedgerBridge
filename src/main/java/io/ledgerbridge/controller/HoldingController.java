package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.HoldingResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.service.HoldingService;
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
@RequestMapping("/v1/users/{user_id}/holdings")
@Tag(name = "Holdings")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @Operation(
            summary = "List current holdings",
            description = """
                    Returns the user's current portfolio holdings with quantities,
                    weighted average cost basis, and broker attribution.

                    Holdings are automatically updated when new transactions are ingested.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Holdings list"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<HoldingResponse>> listHoldings(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @PathVariable("user_id") String userId,
            @Parameter(description = "Pagination cursor from a previous response")
            @RequestParam(value = "cursor", required = false) String cursor,
            @Parameter(description = "Number of items to return (max 100)", example = "25")
            @RequestParam(value = "limit", defaultValue = "25") int limit,
            @Parameter(description = "Filter by broker name", example = "zerodha")
            @RequestParam(value = "broker", required = false) String broker,
            @Parameter(description = "Filter by asset symbol", example = "TCS")
            @RequestParam(value = "symbol", required = false) String symbol) {
        return ResponseEntity.ok(holdingService.listHoldings(
                userId, cursor, Math.min(limit, 100), broker, symbol));
    }

    @Operation(
            summary = "Get a single holding",
            description = "Retrieve detailed information for a specific holding including cost basis and purchase history."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Holding details",
                    content = @Content(schema = @Schema(implementation = HoldingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Holding not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{holding_id}")
    public ResponseEntity<HoldingResponse> getHolding(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @PathVariable("user_id") String userId,
            @Parameter(description = "Holding ID", example = "hld_p6q7r8", required = true)
            @PathVariable("holding_id") String holdingId) {
        return ResponseEntity.ok(holdingService.getHolding(userId, holdingId));
    }
}
