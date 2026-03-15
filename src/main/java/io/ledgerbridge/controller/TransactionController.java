package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.dto.TransactionResponse;
import io.ledgerbridge.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/users/{user_id}/transactions")
@Tag(name = "Transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "List transactions",
            description = """
                    Returns normalized transactions across all connected brokers for a user.
                    Supports filtering by date range, broker, symbol, and trade side.

                    Results are cursor-paginated. Use the `next_cursor` from the response
                    to fetch the next page.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions list"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<TransactionResponse>> listTransactions(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @PathVariable("user_id") String userId,
            @Parameter(description = "Pagination cursor from a previous response")
            @RequestParam(value = "cursor", required = false) String cursor,
            @Parameter(description = "Number of items to return (max 100)", example = "25")
            @RequestParam(value = "limit", defaultValue = "25") int limit,
            @Parameter(description = "Filter transactions on or after this date (inclusive)", example = "2026-01-01")
            @RequestParam(value = "start_date", required = false) LocalDate startDate,
            @Parameter(description = "Filter transactions on or before this date (inclusive)", example = "2026-03-15")
            @RequestParam(value = "end_date", required = false) LocalDate endDate,
            @Parameter(description = "Filter by broker name", example = "zerodha")
            @RequestParam(value = "broker", required = false) String broker,
            @Parameter(description = "Filter by asset symbol", example = "TCS")
            @RequestParam(value = "symbol", required = false) String symbol,
            @Parameter(description = "Filter by trade side", schema = @Schema(allowableValues = {"buy", "sell"}))
            @RequestParam(value = "side", required = false) String side,
            @Parameter(description = "Sort order", schema = @Schema(allowableValues = {"date_asc", "date_desc"}, defaultValue = "date_desc"))
            @RequestParam(value = "sort", defaultValue = "date_desc") String sort) {
        return ResponseEntity.ok(transactionService.listTransactions(
                userId, cursor, Math.min(limit, 100), startDate, endDate, broker, symbol, side, sort));
    }

    @Operation(
            summary = "Get a single transaction",
            description = "Retrieve full details for a specific transaction including charges breakdown."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction details",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{transaction_id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @PathVariable("user_id") String userId,
            @Parameter(description = "Transaction ID", example = "txn_m3n4o5", required = true)
            @PathVariable("transaction_id") String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(userId, transactionId));
    }
}
