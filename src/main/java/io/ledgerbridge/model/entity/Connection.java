package io.ledgerbridge.model.entity;

import io.ledgerbridge.model.enums.ConnectionProvider;
import io.ledgerbridge.model.enums.ConnectionStatus;
import io.ledgerbridge.model.enums.ConnectionType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "connections", indexes = {
    @Index(name = "idx_connections_user_id", columnList = "user_id"),
    @Index(name = "idx_connections_status", columnList = "status")
})
public class Connection extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionStatus status = ConnectionStatus.PENDING;

    @Column(name = "redirect_url", length = 2048)
    private String redirectUrl;

    @Column(name = "access_token_encrypted", length = 2048)
    private String accessTokenEncrypted;

    @Column(name = "refresh_token_encrypted", length = 2048)
    private String refreshTokenEncrypted;

    @Column(name = "credentials_encrypted", length = 2048)
    private String credentialsEncrypted;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Override
    protected String getPrefix() {
        return "conn";
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public ConnectionProvider getProvider() { return provider; }
    public void setProvider(ConnectionProvider provider) { this.provider = provider; }
    public ConnectionType getType() { return type; }
    public void setType(ConnectionType type) { this.type = type; }
    public ConnectionStatus getStatus() { return status; }
    public void setStatus(ConnectionStatus status) { this.status = status; }
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    public String getAccessTokenEncrypted() { return accessTokenEncrypted; }
    public void setAccessTokenEncrypted(String accessTokenEncrypted) { this.accessTokenEncrypted = accessTokenEncrypted; }
    public String getRefreshTokenEncrypted() { return refreshTokenEncrypted; }
    public void setRefreshTokenEncrypted(String refreshTokenEncrypted) { this.refreshTokenEncrypted = refreshTokenEncrypted; }
    public String getCredentialsEncrypted() { return credentialsEncrypted; }
    public void setCredentialsEncrypted(String credentialsEncrypted) { this.credentialsEncrypted = credentialsEncrypted; }
    public Instant getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(Instant lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
