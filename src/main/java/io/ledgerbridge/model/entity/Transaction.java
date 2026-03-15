package io.ledgerbridge.model.entity;

import io.ledgerbridge.model.enums.TradeSide;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_txn_user_id", columnList = "user_id"),
    @Index(name = "idx_txn_user_date", columnList = "user_id, date"),
    @Index(name = "idx_txn_user_broker", columnList = "user_id, broker"),
    @Index(name = "idx_txn_asset_id", columnList = "asset_id")
})
public class Transaction extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "total_value", nullable = false, precision = 18, scale = 4)
    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeSide side;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String broker;

    @Column(nullable = false)
    private String exchange;

    @Column(name = "ingestion_id")
    private String ingestionId;

    @Column(precision = 12, scale = 4)
    private BigDecimal brokerage;

    @Column(precision = 12, scale = 4)
    private BigDecimal stt;

    @Column(precision = 12, scale = 4)
    private BigDecimal gst;

    @Column(name = "stamp_duty", precision = 12, scale = 4)
    private BigDecimal stampDuty;

    @Column(name = "total_charges", precision = 12, scale = 4)
    private BigDecimal totalCharges;

    @Override
    protected String getPrefix() {
        return "txn";
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public TradeSide getSide() { return side; }
    public void setSide(TradeSide side) { this.side = side; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getBroker() { return broker; }
    public void setBroker(String broker) { this.broker = broker; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getIngestionId() { return ingestionId; }
    public void setIngestionId(String ingestionId) { this.ingestionId = ingestionId; }
    public BigDecimal getBrokerage() { return brokerage; }
    public void setBrokerage(BigDecimal brokerage) { this.brokerage = brokerage; }
    public BigDecimal getStt() { return stt; }
    public void setStt(BigDecimal stt) { this.stt = stt; }
    public BigDecimal getGst() { return gst; }
    public void setGst(BigDecimal gst) { this.gst = gst; }
    public BigDecimal getStampDuty() { return stampDuty; }
    public void setStampDuty(BigDecimal stampDuty) { this.stampDuty = stampDuty; }
    public BigDecimal getTotalCharges() { return totalCharges; }
    public void setTotalCharges(BigDecimal totalCharges) { this.totalCharges = totalCharges; }
}
