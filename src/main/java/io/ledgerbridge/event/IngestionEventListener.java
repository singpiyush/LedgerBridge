package io.ledgerbridge.event;

import io.ledgerbridge.webhook.WebhookDispatcher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IngestionEventListener {

    private final WebhookDispatcher webhookDispatcher;

    public IngestionEventListener(WebhookDispatcher webhookDispatcher) {
        this.webhookDispatcher = webhookDispatcher;
    }

    @EventListener
    public void onIngestionCompleted(IngestionCompletedEvent event) {
        webhookDispatcher.dispatch("ingestion.completed", Map.of(
                "ingestion_id", event.ingestionId(),
                "user_id", event.userId(),
                "transactions_extracted", event.transactionsExtracted(),
                "status", "completed"
        ));
    }

    @EventListener
    public void onIngestionFailed(IngestionFailedEvent event) {
        webhookDispatcher.dispatch("ingestion.failed", Map.of(
                "ingestion_id", event.ingestionId(),
                "user_id", event.userId(),
                "error_code", event.errorCode()
        ));
    }

    public record IngestionCompletedEvent(String ingestionId, String userId, int transactionsExtracted) {}
    public record IngestionFailedEvent(String ingestionId, String userId, String errorCode) {}
}
