package io.ledgerbridge.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String errorType;
    private final String errorCode;
    private final String param;

    public ApiException(HttpStatus status, String errorType, String message) {
        super(message);
        this.status = status;
        this.errorType = errorType;
        this.errorCode = null;
        this.param = null;
    }

    public ApiException(HttpStatus status, String errorType, String message, String errorCode, String param) {
        super(message);
        this.status = status;
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.param = param;
    }

    public HttpStatus getStatus() { return status; }
    public String getErrorType() { return errorType; }
    public String getErrorCode() { return errorCode; }
    public String getParam() { return param; }

    public static ApiException notFound(String resource) {
        return new ApiException(HttpStatus.NOT_FOUND, "not_found",
                "The requested " + resource + " was not found", "resource_not_found", null);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "authentication_error", message);
    }

    public static ApiException validation(String message, String param) {
        return new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "validation_error",
                message, "invalid_param", param);
    }

    public static ApiException rateLimited() {
        return new ApiException(HttpStatus.TOO_MANY_REQUESTS, "rate_limit_error",
                "Rate limit exceeded. Please retry after the period indicated in the Retry-After header.");
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, "conflict", message);
    }
}
