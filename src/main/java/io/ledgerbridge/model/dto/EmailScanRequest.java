package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

public record EmailScanRequest(
    @NotBlank @JsonProperty("connection_id") String connectionId,
    LocalDate since,
    List<String> senders
) {}
