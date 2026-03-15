package io.ledgerbridge.service;

import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.dto.TransactionResponse;
import io.ledgerbridge.model.entity.Transaction;
import io.ledgerbridge.model.enums.TradeSide;
import io.ledgerbridge.repository.TransactionRepository;
import io.ledgerbridge.util.EntityMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public PaginatedResponse<TransactionResponse> listTransactions(
            String userId, String cursor, int limit, LocalDate startDate, LocalDate endDate,
            String broker, String symbol, String side, String sort) {

        Sort sorting = "date_asc".equals(sort)
                ? Sort.by("date").ascending().and(Sort.by("id").ascending())
                : Sort.by("date").descending().and(Sort.by("id").descending());

        Specification<Transaction> spec = buildSpec(userId, cursor, startDate, endDate, broker, symbol, side);

        List<Transaction> transactions = transactionRepository.findAll(
                spec, PageRequest.of(0, limit + 1, sorting)).getContent();

        boolean hasMore = transactions.size() > limit;
        if (hasMore) {
            transactions = transactions.subList(0, limit);
        }

        List<TransactionResponse> data = transactions.stream()
                .map(EntityMapper::toTransactionResponse)
                .toList();

        String nextCursor = hasMore ? transactions.get(transactions.size() - 1).getId() : null;

        return PaginatedResponse.of(data, hasMore, nextCursor, null);
    }

    public TransactionResponse getTransaction(String userId, String transactionId) {
        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> ApiException.notFound("transaction"));
        if (!txn.getUserId().equals(userId)) {
            throw ApiException.notFound("transaction");
        }
        return EntityMapper.toTransactionResponse(txn);
    }

    private Specification<Transaction> buildSpec(
            String userId, String cursor, LocalDate startDate, LocalDate endDate,
            String broker, String symbol, String side) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));

            if (cursor != null) {
                predicates.add(cb.greaterThan(root.get("id"), cursor));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (broker != null) {
                predicates.add(cb.equal(cb.lower(root.get("broker")), broker.toLowerCase()));
            }
            if (symbol != null) {
                predicates.add(cb.equal(root.get("asset").get("symbol"), symbol.toUpperCase()));
            }
            if (side != null) {
                predicates.add(cb.equal(root.get("side"), TradeSide.valueOf(side.toUpperCase())));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
