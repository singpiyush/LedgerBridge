package io.ledgerbridge.model.entity;

import io.ledgerbridge.model.enums.JobStatus;
import io.ledgerbridge.model.enums.JobType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "jobs", indexes = {
    @Index(name = "idx_jobs_status", columnList = "status")
})
public class Job extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.QUEUED;

    @Column(nullable = false)
    private int progress = 0;

    @Column(name = "result_data", columnDefinition = "TEXT")
    private String resultData;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "user_id")
    private String userId;

    @Override
    protected String getPrefix() {
        return "job";
    }

    public JobType getType() { return type; }
    public void setType(JobType type) { this.type = type; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getResultData() { return resultData; }
    public void setResultData(String resultData) { this.resultData = resultData; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
