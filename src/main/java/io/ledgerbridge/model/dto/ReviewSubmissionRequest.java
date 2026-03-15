package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReviewSubmissionRequest(
    @NotEmpty List<ReviewItemAction> items
) {
    public record ReviewItemAction(
        @NotNull String id,
        @NotNull String action,
        @JsonProperty("corrected_transaction") TransactionData correctedTransaction
    ) {}
}
