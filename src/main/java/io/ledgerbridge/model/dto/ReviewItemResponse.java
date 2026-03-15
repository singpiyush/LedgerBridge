package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReviewItemResponse(
    String id,
    @JsonProperty("extracted_transaction") TransactionData extractedTransaction,
    Double confidence,
    List<TransactionData> alternatives,
    @JsonProperty("source_snippet") String sourceSnippet
) {}
