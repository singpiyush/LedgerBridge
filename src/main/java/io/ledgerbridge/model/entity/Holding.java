package io.ledgerbridge.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "holdings", indexes = {
    @Index(name = "idx_holdings_user_id", columnList = "user_id"),
    @Index(name = "idx_holdings_user_asset", columnList = "user_id, asset_id, broker", unique = true)
})
public class Holding extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal quantity;

    @Column(name = "avg_price", nullable = false, precision = 18, scale = 4)
    private BigDecimal avgPrice;

    @Column(name = "invested_value", nullable = false, precision = 18, scale = 4)
    private BigDecimal investedValue;

    @Column(nullable = false)
    private String broker;

    @Column(nullable = false)
    private String exchange;

    @Column(name = "first_bought_at")
    private LocalDate firstBoughtAt;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (lastUpdated == null) {
            lastUpdated = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }

    @Override
    protected String getPrefix() {
        return "hld";
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getAvgPrice() { return avgPrice; }
    public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }
    public BigDecimal getInvestedValue() { return investedValue; }
    public void setInvestedValue(BigDecimal investedValue) { this.investedValue = investedValue; }
    public String getBroker() { return broker; }
    public void setBroker(String broker) { this.broker = broker; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public LocalDate getFirstBoughtAt() { return firstBoughtAt; }
    public void setFirstBoughtAt(LocalDate firstBoughtAt) { this.firstBoughtAt = firstBoughtAt; }
    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
