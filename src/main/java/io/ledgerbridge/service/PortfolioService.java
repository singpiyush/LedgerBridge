package io.ledgerbridge.service;

import io.ledgerbridge.model.dto.CapitalGainsResponse;
import io.ledgerbridge.model.dto.PerformanceResponse;
import io.ledgerbridge.model.dto.PortfolioSummaryResponse;
import io.ledgerbridge.model.entity.Holding;
import io.ledgerbridge.model.entity.Transaction;
import io.ledgerbridge.model.enums.TradeSide;
import io.ledgerbridge.repository.HoldingRepository;
import io.ledgerbridge.repository.TransactionRepository;
import io.ledgerbridge.util.EntityMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    public PortfolioService(HoldingRepository holdingRepository, TransactionRepository transactionRepository) {
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
    }

    public PortfolioSummaryResponse getPortfolioSummary(String userId) {
        List<Holding> holdings = holdingRepository.findByUserId(userId);

        BigDecimal totalInvested = BigDecimal.ZERO;
        Set<String> brokers = new HashSet<>();
        Map<String, BigDecimal> sectorValues = new HashMap<>();

        for (Holding h : holdings) {
            totalInvested = totalInvested.add(h.getInvestedValue());
            brokers.add(h.getBroker());

            String sector = h.getAsset().getSector();
            if (sector != null) {
                sectorValues.merge(sector, h.getInvestedValue(), BigDecimal::add);
            }
        }

        // Current value requires market data - using invested as placeholder
        BigDecimal currentValue = totalInvested;
        BigDecimal totalGain = currentValue.subtract(totalInvested);
        BigDecimal totalGainPct = totalInvested.compareTo(BigDecimal.ZERO) > 0
                ? totalGain.multiply(BigDecimal.valueOf(100)).divide(totalInvested, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<PortfolioSummaryResponse.AllocationDto> sectorAllocation = sectorValues.entrySet().stream()
                .map(e -> {
                    BigDecimal pct = totalInvested.compareTo(BigDecimal.ZERO) > 0
                            ? e.getValue().multiply(BigDecimal.valueOf(100))
                                .divide(totalInvested, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new PortfolioSummaryResponse.AllocationDto(e.getKey(), e.getValue(), pct);
                })
                .sorted(Comparator.comparing(PortfolioSummaryResponse.AllocationDto::percentage).reversed())
                .toList();

        return new PortfolioSummaryResponse(
                userId, totalInvested, currentValue, totalGain, totalGainPct,
                BigDecimal.ZERO, totalGain, holdings.size(),
                new ArrayList<>(brokers), sectorAllocation, Instant.now());
    }

    public PerformanceResponse getPerformance(String userId, String period, String benchmark) {
        // Performance tracking requires historical price data
        // Returning structure with empty data points for now
        return new PerformanceResponse(period, List.of(),
                new PerformanceResponse.Summary(BigDecimal.ZERO, null, null, BigDecimal.ZERO));
    }

    public CapitalGainsResponse getCapitalGains(String userId, String financialYear) {
        // Parse financial year (e.g., "2025-26")
        String[] parts = financialYear.split("-");
        int startYear = Integer.parseInt(parts[0]);
        LocalDate fyStart = LocalDate.of(startYear, Month.APRIL, 1);
        LocalDate fyEnd = LocalDate.of(startYear + 1, Month.MARCH, 31);

        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            predicates.add(cb.equal(root.get("side"), TradeSide.SELL));
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), fyStart));
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), fyEnd));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Transaction> sells = transactionRepository.findAll(spec);

        // Simplified gains calculation
        BigDecimal totalGain = BigDecimal.ZERO;
        List<CapitalGainsResponse.GainTransaction> gainTransactions = new ArrayList<>();

        for (Transaction sell : sells) {
            BigDecimal gain = sell.getTotalValue();
            totalGain = totalGain.add(gain);

            gainTransactions.add(new CapitalGainsResponse.GainTransaction(
                    EntityMapper.toAssetRef(sell.getAsset()),
                    null, sell.getDate(), BigDecimal.ZERO, sell.getPrice(),
                    sell.getQuantity(), gain, "short_term"));
        }

        var zeroBreakdown = new CapitalGainsResponse.GainBreakdown(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        return new CapitalGainsResponse(financialYear, zeroBreakdown, zeroBreakdown,
                totalGain, gainTransactions);
    }
}
