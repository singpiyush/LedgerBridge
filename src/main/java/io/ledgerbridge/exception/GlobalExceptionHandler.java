package io.ledgerbridge.exception;

import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.util.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        String requestId = RequestContext.getRequestId();
        ErrorResponse response = ErrorResponse.of(
                ex.getErrorType(), ex.getMessage(), ex.getErrorCode(), ex.getParam(), requestId);
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String requestId = RequestContext.getRequestId();
        String field = ex.getBindingResult().getFieldErrors().isEmpty()
                ? null
                : ex.getBindingResult().getFieldErrors().get(0).getField();
        String message = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : field + ": " + ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ErrorResponse response = ErrorResponse.of("validation_error", message, "invalid_param", field, requestId);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(MaxUploadSizeExceededException ex) {
        String requestId = RequestContext.getRequestId();
        ErrorResponse response = ErrorResponse.of("validation_error",
                "File size exceeds the maximum allowed size of 20 MB", requestId);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        String requestId = RequestContext.getRequestId();
        log.error("Unexpected error [requestId={}]", requestId, ex);
        ErrorResponse response = ErrorResponse.of("server_error",
                "An unexpected error occurred", requestId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
