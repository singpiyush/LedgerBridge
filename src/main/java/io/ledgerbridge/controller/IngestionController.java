package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.*;
import io.ledgerbridge.service.IngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/ingestion")
@Tag(name = "Ingestion")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Operation(
            summary = "Trigger an email scan for a connection",
            description = """
                    Scans the connected email inbox for financial emails (contract notes, portfolio statements,
                    trade confirmations). Matching emails are queued for parsing.

                    Returns an async job that can be polled via `GET /v1/jobs/{job_id}`.

                    **Tip:** Use the `since` parameter to limit the scan window for faster results.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Scan job accepted",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "422", description = "Connection not active or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/email-scan")
    public ResponseEntity<JobResponse> triggerEmailScan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email scan configuration",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "connection_id": "conn_a1b2c3",
                              "since": "2025-01-01"
                            }
                            """)))
            @Valid @RequestBody EmailScanRequest request) {
        JobResponse response = ingestionService.triggerEmailScan(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(
            summary = "Upload a document for parsing",
            description = """
                    Upload a PDF, CSV, or image of a broker statement or contract note.
                    The file is queued for parsing and extracted transactions will appear
                    in the user's transaction list once processing completes.

                    **Supported formats:** `application/pdf`, `text/csv`, `image/png`, `image/jpeg`

                    **Max file size:** 20 MB

                    Use `broker: "auto"` for automatic broker detection.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Upload accepted for processing",
                    content = @Content(schema = @Schema(implementation = IngestionResponse.class))),
            @ApiResponse(responseCode = "413", description = "File too large (max 20 MB)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Invalid broker or document type",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngestionResponse> uploadDocument(
            @Parameter(description = "User ID", example = "usr_k8m2n4", required = true)
            @RequestParam("user_id") String userId,
            @Parameter(description = "The document file (PDF, CSV, PNG, JPEG)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Broker that issued the document", example = "zerodha",
                    schema = @Schema(allowableValues = {"zerodha", "upstox", "groww", "auto"}))
            @RequestParam("broker") String broker,
            @Parameter(description = "Type of document",
                    schema = @Schema(allowableValues = {"contract_note", "holdings_statement", "transaction_history", "other"}))
            @RequestParam(value = "document_type", required = false) String documentType,
            @Parameter(description = "Date of the document (for ordering)", example = "2026-03-15")
            @RequestParam(value = "document_date", required = false) String documentDate) {
        IngestionResponse response = ingestionService.uploadDocument(
                userId, file, broker, documentType, documentDate);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(
            summary = "Get ingestion status and results",
            description = "Retrieve the current status, confidence score, and extraction results for an ingestion."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingestion details",
                    content = @Content(schema = @Schema(implementation = IngestionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ingestion not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{ingestion_id}")
    public ResponseEntity<IngestionResponse> getIngestion(
            @Parameter(description = "Ingestion ID", example = "ing_a1b2c3d4", required = true)
            @PathVariable("ingestion_id") String ingestionId) {
        return ResponseEntity.ok(ingestionService.getIngestion(ingestionId));
    }

    @Operation(
            summary = "Get parsed transactions pending user confirmation",
            description = """
                    When parser confidence is below the auto-accept threshold, extracted transactions
                    are held for review. This endpoint returns those pending items so the end-user
                    can confirm or correct them before they are committed to the portfolio.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review items",
                    content = @Content(schema = @Schema(implementation = ReviewItemsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ingestion not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{ingestion_id}/review")
    public ResponseEntity<ReviewItemsResponse> getReviewItems(
            @Parameter(description = "Ingestion ID", example = "ing_a1b2c3d4", required = true)
            @PathVariable("ingestion_id") String ingestionId) {
        return ResponseEntity.ok(ingestionService.getReviewItems(ingestionId));
    }

    @Operation(
            summary = "Confirm or correct parsed transactions",
            description = """
                    Submit review decisions for each pending extraction. Each item can be:
                    - `accept` — Use the extracted data as-is
                    - `reject` — Discard this extraction
                    - `correct` — Override with user-provided data (include `corrected_transaction`)

                    After review, the portfolio is automatically recomputed.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review accepted, portfolio recomputation queued",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "422", description = "Invalid review action",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{ingestion_id}/review")
    public ResponseEntity<JobResponse> submitReview(
            @Parameter(description = "Ingestion ID", example = "ing_a1b2c3d4", required = true)
            @PathVariable("ingestion_id") String ingestionId,
            @Valid @RequestBody ReviewSubmissionRequest request) {
        JobResponse response = ingestionService.submitReview(ingestionId, request);
        return ResponseEntity.ok(response);
    }
}
