package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.*;
import io.ledgerbridge.service.IngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/ingestion")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/email-scan")
    public ResponseEntity<JobResponse> triggerEmailScan(
            @Valid @RequestBody EmailScanRequest request) {
        JobResponse response = ingestionService.triggerEmailScan(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<IngestionResponse> uploadDocument(
            @RequestParam("user_id") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("broker") String broker,
            @RequestParam(value = "document_type", required = false) String documentType,
            @RequestParam(value = "document_date", required = false) String documentDate) {
        IngestionResponse response = ingestionService.uploadDocument(
                userId, file, broker, documentType, documentDate);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{ingestion_id}")
    public ResponseEntity<IngestionResponse> getIngestion(
            @PathVariable("ingestion_id") String ingestionId) {
        return ResponseEntity.ok(ingestionService.getIngestion(ingestionId));
    }

    @GetMapping("/{ingestion_id}/review")
    public ResponseEntity<ReviewItemsResponse> getReviewItems(
            @PathVariable("ingestion_id") String ingestionId) {
        return ResponseEntity.ok(ingestionService.getReviewItems(ingestionId));
    }

    @PostMapping("/{ingestion_id}/review")
    public ResponseEntity<JobResponse> submitReview(
            @PathVariable("ingestion_id") String ingestionId,
            @Valid @RequestBody ReviewSubmissionRequest request) {
        JobResponse response = ingestionService.submitReview(ingestionId, request);
        return ResponseEntity.ok(response);
    }
}
