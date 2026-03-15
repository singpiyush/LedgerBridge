package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.dto.WebhookCreateRequest;
import io.ledgerbridge.model.dto.WebhookResponse;
import io.ledgerbridge.model.dto.WebhookUpdateRequest;
import io.ledgerbridge.service.WebhookService;
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
@RequestMapping("/v1/webhooks")
@Tag(name = "Webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Operation(
            summary = "List webhook subscriptions",
            description = "Returns all webhook subscriptions for the authenticated account."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook list")
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<WebhookResponse>> listWebhooks(
            @Parameter(description = "Pagination cursor from a previous response")
            @RequestParam(value = "cursor", required = false) String cursor,
            @Parameter(description = "Number of items to return (max 100)", example = "25")
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return ResponseEntity.ok(webhookService.listWebhooks(cursor, Math.min(limit, 100)));
    }

    @Operation(
            summary = "Create a webhook subscription",
            description = """
                    Subscribe to specific events. All payloads are signed with HMAC-SHA256
                    using the webhook secret.

                    **Available events:**
                    - `ingestion.completed` — Document parsing finished successfully
                    - `ingestion.failed` — Document parsing failed
                    - `ingestion.review_required` — Parsed data needs user confirmation
                    - `transaction.created` — New transaction added to portfolio
                    - `holdings.updated` — Holdings recalculated
                    - `portfolio.recomputed` — Full portfolio recomputation completed
                    - `connection.status_changed` — Connection status changed (active, expired, error)

                    **Signature verification:**
                    ```
                    expected = HMAC-SHA256(webhook_secret, raw_body)
                    verify: X-LedgerBridge-Signature == "sha256=" + expected
                    ```

                    The `secret` is only returned in the creation response. Store it securely.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Webhook created (includes secret)",
                    content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<WebhookResponse> createWebhook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Webhook subscription details",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "url": "https://yourapp.com/webhooks/ledgerbridge",
                              "events": [
                                "ingestion.completed",
                                "ingestion.review_required",
                                "holdings.updated"
                              ]
                            }
                            """)))
            @Valid @RequestBody WebhookCreateRequest request) {
        WebhookResponse response = webhookService.createWebhook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get webhook details",
            description = "Retrieve configuration for a specific webhook subscription. The secret is not returned."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook details",
                    content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Webhook not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{webhook_id}")
    public ResponseEntity<WebhookResponse> getWebhook(
            @Parameter(description = "Webhook ID", example = "whk_d4e5f6", required = true)
            @PathVariable("webhook_id") String webhookId) {
        return ResponseEntity.ok(webhookService.getWebhook(webhookId));
    }

    @Operation(
            summary = "Update a webhook subscription",
            description = "Partially update a webhook. Only provided fields are changed. Use `enabled: false` to pause delivery."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook updated",
                    content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Webhook not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{webhook_id}")
    public ResponseEntity<WebhookResponse> updateWebhook(
            @Parameter(description = "Webhook ID", example = "whk_d4e5f6", required = true)
            @PathVariable("webhook_id") String webhookId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fields to update",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "enabled": false
                            }
                            """)))
            @RequestBody WebhookUpdateRequest request) {
        return ResponseEntity.ok(webhookService.updateWebhook(webhookId, request));
    }

    @Operation(
            summary = "Delete a webhook subscription",
            description = "Permanently remove a webhook subscription. No further events will be delivered to this URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Webhook deleted"),
            @ApiResponse(responseCode = "404", description = "Webhook not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{webhook_id}")
    public ResponseEntity<Void> deleteWebhook(
            @Parameter(description = "Webhook ID", example = "whk_d4e5f6", required = true)
            @PathVariable("webhook_id") String webhookId) {
        webhookService.deleteWebhook(webhookId);
        return ResponseEntity.noContent().build();
    }
}
