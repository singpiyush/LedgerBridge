package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error response envelope")
public record ErrorResponse(ErrorBody error) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Error details")
    public record ErrorBody(
        @Schema(description = "Error category", example = "validation_error",
                allowableValues = {"authentication_error", "authorization_error", "validation_error",
                        "not_found", "rate_limit_error", "conflict", "server_error"})
        String type,

        @Schema(description = "Human-readable error message", example = "The requested resource was not found")
        String message,

        @Schema(description = "Machine-readable error code", example = "resource_not_found")
        String code,

        @Schema(description = "The parameter that caused the error", example = "broker")
        String param,

        @Schema(description = "Unique request identifier for debugging", example = "req_abc123def456")
        @JsonProperty("request_id") String requestId
    ) {}

    public static ErrorResponse of(String type, String message, String requestId) {
        return new ErrorResponse(new ErrorBody(type, message, null, null, requestId));
    }

    public static ErrorResponse of(String type, String message, String code, String param, String requestId) {
        return new ErrorResponse(new ErrorBody(type, message, code, param, requestId));
    }
}
