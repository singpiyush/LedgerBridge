package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.ConnectionCreateRequest;
import io.ledgerbridge.model.dto.ConnectionResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ConnectionResponse>> listConnections(
            @RequestParam("user_id") String userId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return ResponseEntity.ok(connectionService.listConnections(userId, cursor, Math.min(limit, 100)));
    }

    @PostMapping
    public ResponseEntity<ConnectionResponse> createConnection(
            @Valid @RequestBody ConnectionCreateRequest request) {
        ConnectionResponse response = connectionService.createConnection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{connection_id}")
    public ResponseEntity<ConnectionResponse> getConnection(
            @PathVariable("connection_id") String connectionId) {
        return ResponseEntity.ok(connectionService.getConnection(connectionId));
    }

    @DeleteMapping("/{connection_id}")
    public ResponseEntity<Void> deleteConnection(
            @PathVariable("connection_id") String connectionId) {
        connectionService.deleteConnection(connectionId);
        return ResponseEntity.noContent().build();
    }
}
