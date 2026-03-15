package io.ledgerbridge.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_keys")
public class ApiKey extends BaseEntity {

    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    @Column(name = "key_prefix", nullable = false, length = 12)
    private String keyPrefix;

    @Column(nullable = false)
    private String name;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String plan;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Override
    protected String getPrefix() {
        return "key";
    }

    public String getKeyHash() { return keyHash; }
    public void setKeyHash(String keyHash) { this.keyHash = keyHash; }
    public String getKeyPrefix() { return keyPrefix; }
    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
}
