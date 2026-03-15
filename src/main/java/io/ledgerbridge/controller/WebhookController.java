package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.dto.WebhookCreateRequest;
import io.ledgerbridge.model.dto.WebhookResponse;
import io.ledgerbridge.model.dto.WebhookUpdateRequest;
import io.ledgerbridge.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<WebhookResponse>> listWebhooks(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return ResponseEntity.ok(webhookService.listWebhooks(cursor, Math.min(limit, 100)));
    }

    @PostMapping
    public ResponseEntity<WebhookResponse> createWebhook(
            @Valid @RequestBody WebhookCreateRequest request) {
        WebhookResponse response = webhookService.createWebhook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{webhook_id}")
    public ResponseEntity<WebhookResponse> getWebhook(
            @PathVariable("webhook_id") String webhookId) {
        return ResponseEntity.ok(webhookService.getWebhook(webhookId));
    }

    @PatchMapping("/{webhook_id}")
    public ResponseEntity<WebhookResponse> updateWebhook(
            @PathVariable("webhook_id") String webhookId,
            @RequestBody WebhookUpdateRequest request) {
        return ResponseEntity.ok(webhookService.updateWebhook(webhookId, request));
    }

    @DeleteMapping("/{webhook_id}")
    public ResponseEntity<Void> deleteWebhook(
            @PathVariable("webhook_id") String webhookId) {
        webhookService.deleteWebhook(webhookId);
        return ResponseEntity.noContent().build();
    }
}
