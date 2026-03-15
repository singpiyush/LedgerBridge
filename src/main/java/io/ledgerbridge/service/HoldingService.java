package io.ledgerbridge.service;

import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.HoldingResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.entity.Holding;
import io.ledgerbridge.repository.HoldingRepository;
import io.ledgerbridge.util.EntityMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HoldingService {

    private final HoldingRepository holdingRepository;

    public HoldingService(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    public PaginatedResponse<HoldingResponse> listHoldings(
            String userId, String cursor, int limit, String broker, String symbol) {

        Specification<Holding> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));

            if (cursor != null) {
                predicates.add(cb.greaterThan(root.get("id"), cursor));
            }
            if (broker != null) {
                predicates.add(cb.equal(cb.lower(root.get("broker")), broker.toLowerCase()));
            }
            if (symbol != null) {
                predicates.add(cb.equal(root.get("asset").get("symbol"), symbol.toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Holding> holdings = holdingRepository.findAll(
                spec, PageRequest.of(0, limit + 1, Sort.by("id").ascending())).getContent();

        boolean hasMore = holdings.size() > limit;
        if (hasMore) {
            holdings = holdings.subList(0, limit);
        }

        List<HoldingResponse> data = holdings.stream()
                .map(EntityMapper::toHoldingResponse)
                .toList();

        String nextCursor = hasMore ? holdings.get(holdings.size() - 1).getId() : null;

        return PaginatedResponse.of(data, hasMore, nextCursor, null);
    }

    public HoldingResponse getHolding(String userId, String holdingId) {
        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> ApiException.notFound("holding"));
        if (!holding.getUserId().equals(userId)) {
            throw ApiException.notFound("holding");
        }
        return EntityMapper.toHoldingResponse(holding);
    }
}
