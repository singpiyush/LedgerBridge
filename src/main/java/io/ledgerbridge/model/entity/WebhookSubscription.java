package io.ledgerbridge.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "webhook_subscriptions", indexes = {
    @Index(name = "idx_webhooks_account", columnList = "account_id")
})
public class WebhookSubscription extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(nullable = false)
    private String events;

    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @Column(name = "secret_encrypted", nullable = false)
    private String secretEncrypted;

    @Column(nullable = false)
    private boolean enabled = true;

    @Override
    protected String getPrefix() {
        return "whk";
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getEvents() { return events; }
    public void setEvents(String events) { this.events = events; }
    public String getSecretHash() { return secretHash; }
    public void setSecretHash(String secretHash) { this.secretHash = secretHash; }
    public String getSecretEncrypted() { return secretEncrypted; }
    public void setSecretEncrypted(String secretEncrypted) { this.secretEncrypted = secretEncrypted; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
