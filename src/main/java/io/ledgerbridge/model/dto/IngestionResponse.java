package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IngestionResponse(
    String id,
    @JsonProperty("user_id") String userId,
    String source,
    String status,
    String broker,
    @JsonProperty("document_type") String documentType,
    @JsonProperty("transactions_extracted") Integer transactionsExtracted,
    @JsonProperty("transactions_confirmed") Integer transactionsConfirmed,
    Double confidence,
    @JsonProperty("job_id") String jobId,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("completed_at") Instant completedAt,
    IngestionErrorDto error
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record IngestionErrorDto(String code, String message) {}
}
