package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobResponse(
    String id,
    String type,
    String status,
    int progress,
    JobResult result,
    @JsonProperty("created_at") Instant createdAt,
    @JsonProperty("completed_at") Instant completedAt,
    JobError error
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JobResult(
        @JsonProperty("ingestion_id") String ingestionId,
        @JsonProperty("transactions_count") Integer transactionsCount,
        @JsonProperty("errors_count") Integer errorsCount
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JobError(String code, String message) {}
}
