package io.ledgerbridge.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionData(
    String symbol,
    String exchange,
    BigDecimal quantity,
    BigDecimal price,
    String side,
    LocalDate date,
    String broker
) {}
