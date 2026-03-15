package io.ledgerbridge.service;

import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.dto.WebhookCreateRequest;
import io.ledgerbridge.model.dto.WebhookResponse;
import io.ledgerbridge.model.dto.WebhookUpdateRequest;
import io.ledgerbridge.model.entity.WebhookSubscription;
import io.ledgerbridge.repository.WebhookSubscriptionRepository;
import io.ledgerbridge.util.RequestContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class WebhookService {

    private final WebhookSubscriptionRepository webhookRepository;

    public WebhookService(WebhookSubscriptionRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    public PaginatedResponse<WebhookResponse> listWebhooks(String cursor, int limit) {
        String accountId = RequestContext.getAccountId();
        List<WebhookSubscription> webhooks;

        if (cursor != null) {
            webhooks = webhookRepository.findByAccountIdAndIdGreaterThanOrderByIdAsc(
                    accountId, cursor, PageRequest.of(0, limit + 1));
        } else {
            webhooks = webhookRepository.findByAccountIdOrderByIdAsc(
                    accountId, PageRequest.of(0, limit + 1));
        }

        boolean hasMore = webhooks.size() > limit;
        if (hasMore) {
            webhooks = webhooks.subList(0, limit);
        }

        List<WebhookResponse> data = webhooks.stream()
                .map(w -> toResponse(w, false))
                .toList();

        String nextCursor = hasMore ? webhooks.get(webhooks.size() - 1).getId() : null;
        long total = webhookRepository.countByAccountId(accountId);

        return PaginatedResponse.of(data, hasMore, nextCursor, total);
    }

    @Transactional
    public WebhookResponse createWebhook(WebhookCreateRequest request) {
        String accountId = RequestContext.getAccountId();

        String secret = request.secret();
        if (secret == null || secret.isBlank()) {
            secret = generateSecret();
        }

        WebhookSubscription webhook = new WebhookSubscription();
        webhook.setAccountId(accountId);
        webhook.setUrl(request.url());
        webhook.setEvents(String.join(",", request.events()));
        webhook.setSecretHash(sha256(secret));
        webhook.setSecretEncrypted(secret); // In production: encrypt with KMS
        webhook = webhookRepository.save(webhook);

        return toResponse(webhook, true, secret);
    }

    public WebhookResponse getWebhook(String webhookId) {
        WebhookSubscription webhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> ApiException.notFound("webhook"));
        return toResponse(webhook, false);
    }

    @Transactional
    public WebhookResponse updateWebhook(String webhookId, WebhookUpdateRequest request) {
        WebhookSubscription webhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> ApiException.notFound("webhook"));

        if (request.url() != null) {
            webhook.setUrl(request.url());
        }
        if (request.events() != null) {
            webhook.setEvents(String.join(",", request.events()));
        }
        if (request.enabled() != null) {
            webhook.setEnabled(request.enabled());
        }

        webhook = webhookRepository.save(webhook);
        return toResponse(webhook, false);
    }

    @Transactional
    public void deleteWebhook(String webhookId) {
        WebhookSubscription webhook = webhookRepository.findById(webhookId)
                .orElseThrow(() -> ApiException.notFound("webhook"));
        webhookRepository.delete(webhook);
    }

    private WebhookResponse toResponse(WebhookSubscription w, boolean includeSecret) {
        return toResponse(w, includeSecret, null);
    }

    private WebhookResponse toResponse(WebhookSubscription w, boolean includeSecret, String rawSecret) {
        List<String> events = Arrays.asList(w.getEvents().split(","));
        String secret = includeSecret ? rawSecret : null;
        return new WebhookResponse(w.getId(), w.getUrl(), events, secret, w.isEnabled(), w.getCreatedAt());
    }

    private String generateSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return "whsec_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
