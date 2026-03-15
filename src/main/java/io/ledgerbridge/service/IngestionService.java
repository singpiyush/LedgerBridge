package io.ledgerbridge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.*;
import io.ledgerbridge.model.entity.Connection;
import io.ledgerbridge.model.entity.Ingestion;
import io.ledgerbridge.model.entity.Job;
import io.ledgerbridge.model.entity.ReviewItem;
import io.ledgerbridge.model.enums.*;
import io.ledgerbridge.repository.ConnectionRepository;
import io.ledgerbridge.repository.IngestionRepository;
import io.ledgerbridge.repository.JobRepository;
import io.ledgerbridge.repository.ReviewItemRepository;
import io.ledgerbridge.util.EntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class IngestionService {

    private final IngestionRepository ingestionRepository;
    private final ConnectionRepository connectionRepository;
    private final JobRepository jobRepository;
    private final ReviewItemRepository reviewItemRepository;
    private final ObjectMapper objectMapper;

    public IngestionService(IngestionRepository ingestionRepository,
                            ConnectionRepository connectionRepository,
                            JobRepository jobRepository,
                            ReviewItemRepository reviewItemRepository,
                            ObjectMapper objectMapper) {
        this.ingestionRepository = ingestionRepository;
        this.connectionRepository = connectionRepository;
        this.jobRepository = jobRepository;
        this.reviewItemRepository = reviewItemRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public JobResponse triggerEmailScan(EmailScanRequest request) {
        Connection connection = connectionRepository.findById(request.connectionId())
                .orElseThrow(() -> ApiException.notFound("connection"));

        if (connection.getStatus() != ConnectionStatus.ACTIVE) {
            throw ApiException.validation("Connection is not active", "connection_id");
        }

        Job job = new Job();
        job.setType(JobType.EMAIL_SCAN);
        job.setStatus(JobStatus.QUEUED);
        job.setUserId(connection.getUserId());
        job = jobRepository.save(job);

        Ingestion ingestion = new Ingestion();
        ingestion.setUserId(connection.getUserId());
        ingestion.setSource(IngestionSource.EMAIL);
        ingestion.setStatus(IngestionStatus.QUEUED);
        ingestion.setConnectionId(request.connectionId());
        ingestion.setJobId(job.getId());
        ingestionRepository.save(ingestion);

        return EntityMapper.toJobResponse(job);
    }

    @Transactional
    public IngestionResponse uploadDocument(String userId, MultipartFile file,
                                             String broker, String documentType, String documentDate) {
        Job job = new Job();
        job.setType(JobType.DOCUMENT_PARSE);
        job.setStatus(JobStatus.QUEUED);
        job.setUserId(userId);
        job = jobRepository.save(job);

        Ingestion ingestion = new Ingestion();
        ingestion.setUserId(userId);
        ingestion.setSource(IngestionSource.UPLOAD);
        ingestion.setStatus(IngestionStatus.QUEUED);
        ingestion.setBroker(broker);
        ingestion.setJobId(job.getId());

        if (documentType != null) {
            try {
                ingestion.setDocumentType(DocumentType.valueOf(documentType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw ApiException.validation("Invalid document_type: " + documentType, "document_type");
            }
        }

        // In production: store file to object storage, set filePath
        ingestion.setFilePath("/uploads/" + file.getOriginalFilename());
        ingestion = ingestionRepository.save(ingestion);

        return EntityMapper.toIngestionResponse(ingestion);
    }

    public IngestionResponse getIngestion(String ingestionId) {
        Ingestion ingestion = ingestionRepository.findById(ingestionId)
                .orElseThrow(() -> ApiException.notFound("ingestion"));
        return EntityMapper.toIngestionResponse(ingestion);
    }

    public ReviewItemsResponse getReviewItems(String ingestionId) {
        ingestionRepository.findById(ingestionId)
                .orElseThrow(() -> ApiException.notFound("ingestion"));

        List<ReviewItem> items = reviewItemRepository.findByIngestionIdAndResolvedFalse(ingestionId);
        List<ReviewItemResponse> responses = items.stream()
                .map(this::toReviewItemResponse)
                .toList();
        return new ReviewItemsResponse(ingestionId, responses);
    }

    @Transactional
    public JobResponse submitReview(String ingestionId, ReviewSubmissionRequest request) {
        Ingestion ingestion = ingestionRepository.findById(ingestionId)
                .orElseThrow(() -> ApiException.notFound("ingestion"));

        for (ReviewSubmissionRequest.ReviewItemAction action : request.items()) {
            ReviewItem item = reviewItemRepository.findById(action.id())
                    .orElseThrow(() -> ApiException.notFound("review_item"));

            ReviewAction reviewAction;
            try {
                reviewAction = ReviewAction.valueOf(action.action().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw ApiException.validation("Invalid action: " + action.action(), "action");
            }

            item.setAction(reviewAction);
            if (reviewAction == ReviewAction.CORRECT && action.correctedTransaction() != null) {
                try {
                    item.setCorrectedData(objectMapper.writeValueAsString(action.correctedTransaction()));
                } catch (JsonProcessingException e) {
                    throw ApiException.validation("Invalid corrected_transaction data", "corrected_transaction");
                }
            }
            item.setResolved(true);
            reviewItemRepository.save(item);
        }

        // Trigger portfolio recomputation
        Job job = new Job();
        job.setType(JobType.PORTFOLIO_RECOMPUTE);
        job.setStatus(JobStatus.QUEUED);
        job.setUserId(ingestion.getUserId());
        job = jobRepository.save(job);

        ingestion.setStatus(IngestionStatus.COMPLETED);
        ingestionRepository.save(ingestion);

        return EntityMapper.toJobResponse(job);
    }

    private ReviewItemResponse toReviewItemResponse(ReviewItem item) {
        TransactionData extracted = null;
        List<TransactionData> alternatives = null;
        try {
            extracted = objectMapper.readValue(item.getExtractedData(), TransactionData.class);
            if (item.getAlternatives() != null) {
                alternatives = objectMapper.readValue(item.getAlternatives(),
                        new TypeReference<List<TransactionData>>() {});
            }
        } catch (JsonProcessingException ignored) {
        }
        return new ReviewItemResponse(item.getId(), extracted, item.getConfidence(),
                alternatives, item.getSourceSnippet());
    }
}
