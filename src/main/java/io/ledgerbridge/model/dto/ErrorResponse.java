package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(ErrorBody error) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorBody(
        String type,
        String message,
        String code,
        String param,
        @JsonProperty("request_id") String requestId
    ) {}

    public static ErrorResponse of(String type, String message, String requestId) {
        return new ErrorResponse(new ErrorBody(type, message, null, null, requestId));
    }

    public static ErrorResponse of(String type, String message, String code, String param, String requestId) {
        return new ErrorResponse(new ErrorBody(type, message, code, param, requestId));
    }
}
