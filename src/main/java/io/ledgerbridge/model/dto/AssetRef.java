package io.ledgerbridge.model.dto;

public record AssetRef(
    String id,
    String symbol,
    String name,
    String exchange
) {}
