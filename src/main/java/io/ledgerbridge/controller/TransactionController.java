package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.dto.TransactionResponse;
import io.ledgerbridge.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/users/{user_id}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<TransactionResponse>> listTransactions(
            @PathVariable("user_id") String userId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "25") int limit,
            @RequestParam(value = "start_date", required = false) LocalDate startDate,
            @RequestParam(value = "end_date", required = false) LocalDate endDate,
            @RequestParam(value = "broker", required = false) String broker,
            @RequestParam(value = "symbol", required = false) String symbol,
            @RequestParam(value = "side", required = false) String side,
            @RequestParam(value = "sort", defaultValue = "date_desc") String sort) {
        return ResponseEntity.ok(transactionService.listTransactions(
                userId, cursor, Math.min(limit, 100), startDate, endDate, broker, symbol, side, sort));
    }

    @GetMapping("/{transaction_id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable("user_id") String userId,
            @PathVariable("transaction_id") String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(userId, transactionId));
    }
}
