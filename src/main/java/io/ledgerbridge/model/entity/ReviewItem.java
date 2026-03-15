package io.ledgerbridge.model.entity;

import io.ledgerbridge.model.enums.ReviewAction;
import jakarta.persistence.*;

@Entity
@Table(name = "review_items", indexes = {
    @Index(name = "idx_review_items_ingestion", columnList = "ingestion_id")
})
public class ReviewItem extends BaseEntity {

    @Column(name = "ingestion_id", nullable = false)
    private String ingestionId;

    @Column(name = "extracted_data", nullable = false, columnDefinition = "TEXT")
    private String extractedData;

    @Column(columnDefinition = "TEXT")
    private String alternatives;

    @Column(name = "source_snippet", columnDefinition = "TEXT")
    private String sourceSnippet;

    @Column(precision = 5)
    private Double confidence;

    @Enumerated(EnumType.STRING)
    private ReviewAction action;

    @Column(name = "corrected_data", columnDefinition = "TEXT")
    private String correctedData;

    private boolean resolved;

    @Override
    protected String getPrefix() {
        return "rev";
    }

    public String getIngestionId() { return ingestionId; }
    public void setIngestionId(String ingestionId) { this.ingestionId = ingestionId; }
    public String getExtractedData() { return extractedData; }
    public void setExtractedData(String extractedData) { this.extractedData = extractedData; }
    public String getAlternatives() { return alternatives; }
    public void setAlternatives(String alternatives) { this.alternatives = alternatives; }
    public String getSourceSnippet() { return sourceSnippet; }
    public void setSourceSnippet(String sourceSnippet) { this.sourceSnippet = sourceSnippet; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public ReviewAction getAction() { return action; }
    public void setAction(ReviewAction action) { this.action = action; }
    public String getCorrectedData() { return correctedData; }
    public void setCorrectedData(String correctedData) { this.correctedData = correctedData; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}
