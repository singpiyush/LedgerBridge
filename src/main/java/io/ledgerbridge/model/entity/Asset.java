package io.ledgerbridge.model.entity;

import io.ledgerbridge.model.enums.AssetType;
import jakarta.persistence.*;

@Entity
@Table(name = "assets", indexes = {
    @Index(name = "idx_assets_symbol", columnList = "symbol"),
    @Index(name = "idx_assets_isin", columnList = "isin", unique = true)
})
public class Asset extends BaseEntity {

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String exchange;

    @Column(unique = true)
    private String isin;

    private String sector;
    private String industry;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 2)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;

    @Override
    protected String getPrefix() {
        return "ast";
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getIsin() { return isin; }
    public void setIsin(String isin) { this.isin = isin; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public AssetType getAssetType() { return assetType; }
    public void setAssetType(AssetType assetType) { this.assetType = assetType; }
}
