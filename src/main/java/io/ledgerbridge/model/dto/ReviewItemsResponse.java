package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ReviewItemsResponse(
    @JsonProperty("ingestion_id") String ingestionId,
    List<ReviewItemResponse> items
) {}
