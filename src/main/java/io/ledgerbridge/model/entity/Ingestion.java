package io.ledgerbridge.model.entity;

import io.ledgerbridge.model.enums.DocumentType;
import io.ledgerbridge.model.enums.IngestionSource;
import io.ledgerbridge.model.enums.IngestionStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ingestions", indexes = {
    @Index(name = "idx_ingestions_user_id", columnList = "user_id"),
    @Index(name = "idx_ingestions_status", columnList = "status")
})
public class Ingestion extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngestionSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngestionStatus status = IngestionStatus.QUEUED;

    private String broker;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @Column(name = "transactions_extracted")
    private Integer transactionsExtracted;

    @Column(name = "transactions_confirmed")
    private Integer transactionsConfirmed;

    @Column(precision = 5)
    private Double confidence;

    @Column(name = "job_id")
    private String jobId;

    @Column(name = "connection_id")
    private String connectionId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Override
    protected String getPrefix() {
        return "ing";
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public IngestionSource getSource() { return source; }
    public void setSource(IngestionSource source) { this.source = source; }
    public IngestionStatus getStatus() { return status; }
    public void setStatus(IngestionStatus status) { this.status = status; }
    public String getBroker() { return broker; }
    public void setBroker(String broker) { this.broker = broker; }
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    public Integer getTransactionsExtracted() { return transactionsExtracted; }
    public void setTransactionsExtracted(Integer transactionsExtracted) { this.transactionsExtracted = transactionsExtracted; }
    public Integer getTransactionsConfirmed() { return transactionsConfirmed; }
    public void setTransactionsConfirmed(Integer transactionsConfirmed) { this.transactionsConfirmed = transactionsConfirmed; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getConnectionId() { return connectionId; }
    public void setConnectionId(String connectionId) { this.connectionId = connectionId; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
