package io.ledgerbridge.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Request to create a webhook subscription")
public record WebhookCreateRequest(
    @Schema(description = "The URL to receive webhook payloads", example = "https://yourapp.com/webhooks/ledgerbridge",
            format = "uri", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String url,

    @Schema(description = "Events to subscribe to", example = "[\"ingestion.completed\", \"holdings.updated\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty List<String> events,

    @Schema(description = "Custom signing secret. If omitted, one is generated automatically.")
    String secret
) {}
