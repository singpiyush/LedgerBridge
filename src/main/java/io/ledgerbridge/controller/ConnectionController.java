package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.ConnectionCreateRequest;
import io.ledgerbridge.model.dto.ConnectionResponse;
import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.service.ConnectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/connections")
@Tag(name = "Connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Operation(
            summary = "List all connections for a user",
            description = "Returns all email and broker connections linked to the specified user, ordered by creation time."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connections list"),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<ConnectionResponse>> listConnections(
            @Parameter(description = "The user ID to scope the request to", example = "usr_k8m2n4", required = true)
            @RequestParam("user_id") String userId,
            @Parameter(description = "Pagination cursor from a previous response")
            @RequestParam(value = "cursor", required = false) String cursor,
            @Parameter(description = "Number of items to return (max 100)", example = "25")
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return ResponseEntity.ok(connectionService.listConnections(userId, cursor, Math.min(limit, 100)));
    }

    @Operation(
            summary = "Create a new email or broker connection",
            description = """
                    Initiates an OAuth flow for email access or stores broker credentials.

                    For **email** connections (`type: "email"`): Returns a `redirect_url` that the end-user
                    must visit to grant read-only email access. Requires `redirect_uri` in the request.

                    For **broker** connections (`type: "broker"`): Stores the provided credentials and
                    activates the connection immediately. Requires `credentials` in the request.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Connection created",
                    content = @Content(schema = @Schema(implementation = ConnectionResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ConnectionResponse> createConnection(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Connection details",
                    content = @Content(examples = {
                            @ExampleObject(name = "Email (Gmail)", value = """
                                    {
                                      "user_id": "usr_k8m2n4",
                                      "provider": "gmail",
                                      "type": "email",
                                      "redirect_uri": "https://yourapp.com/callback"
                                    }
                                    """),
                            @ExampleObject(name = "Broker (Zerodha)", value = """
                                    {
                                      "user_id": "usr_k8m2n4",
                                      "provider": "zerodha",
                                      "type": "broker",
                                      "credentials": {
                                        "client_id": "AB1234",
                                        "api_secret": "your-api-secret"
                                      }
                                    }
                                    """)
                    }))
            @Valid @RequestBody ConnectionCreateRequest request,
            @Parameter(description = "Idempotency key for safe retries", example = "req_unique_abc123")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        ConnectionResponse response = connectionService.createConnection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get connection details",
            description = "Retrieve the current status and metadata for a specific connection."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connection details",
                    content = @Content(schema = @Schema(implementation = ConnectionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Connection not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{connection_id}")
    public ResponseEntity<ConnectionResponse> getConnection(
            @Parameter(description = "Connection ID", example = "conn_a1b2c3", required = true)
            @PathVariable("connection_id") String connectionId) {
        return ResponseEntity.ok(connectionService.getConnection(connectionId));
    }

    @Operation(
            summary = "Revoke and delete a connection",
            description = """
                    Revokes OAuth tokens, deletes stored credentials, and removes all data ingested
                    through this connection. **This action is irreversible.**
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Connection deleted"),
            @ApiResponse(responseCode = "404", description = "Connection not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{connection_id}")
    public ResponseEntity<Void> deleteConnection(
            @Parameter(description = "Connection ID", example = "conn_a1b2c3", required = true)
            @PathVariable("connection_id") String connectionId) {
        connectionService.deleteConnection(connectionId);
        return ResponseEntity.noContent().build();
    }
}
