package io.ledgerbridge.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ledgerbridge.model.entity.WebhookSubscription;
import io.ledgerbridge.repository.WebhookSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class WebhookDispatcher {

    private static final Logger log = LoggerFactory.getLogger(WebhookDispatcher.class);

    private final WebhookSubscriptionRepository webhookRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public WebhookDispatcher(WebhookSubscriptionRepository webhookRepository, ObjectMapper objectMapper) {
        this.webhookRepository = webhookRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Async("webhookExecutor")
    public void dispatch(String eventType, Map<String, Object> data) {
        webhookRepository.findByEnabledTrue().stream()
                .filter(w -> w.getEvents().contains(eventType))
                .forEach(w -> deliverWithRetry(w, eventType, data, 3));
    }

    private void deliverWithRetry(WebhookSubscription webhook, String eventType,
                                   Map<String, Object> data, int maxRetries) {
        Map<String, Object> payload = Map.of(
                "id", "evt_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12),
                "type", eventType,
                "created_at", Instant.now().toString(),
                "data", data
        );

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                String body = objectMapper.writeValueAsString(payload);
                String signature = sign(body, webhook.getSecretEncrypted());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(webhook.getUrl()))
                        .header("Content-Type", "application/json")
                        .header("X-LedgerBridge-Signature", "sha256=" + signature)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return;
                }

                log.warn("Webhook delivery failed [url={}, status={}, attempt={}]",
                        webhook.getUrl(), response.statusCode(), attempt);
            } catch (Exception e) {
                log.warn("Webhook delivery error [url={}, attempt={}]: {}",
                        webhook.getUrl(), attempt, e.getMessage());
            }

            if (attempt < maxRetries) {
                try {
                    Thread.sleep((long) Math.pow(2, attempt) * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private String sign(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 signing failed", e);
        }
    }
}
