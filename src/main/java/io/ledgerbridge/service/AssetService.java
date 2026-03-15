package io.ledgerbridge.service;

import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.AssetResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.model.entity.Asset;
import io.ledgerbridge.repository.AssetRepository;
import io.ledgerbridge.util.EntityMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public PaginatedResponse<AssetResponse> listAssets(
            String q, String exchange, String sector, String cursor, int limit) {

        Specification<Asset> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("symbol")), pattern),
                        cb.like(cb.lower(root.get("name")), pattern)
                ));
            }
            if (exchange != null) {
                predicates.add(cb.equal(cb.upper(root.get("exchange")), exchange.toUpperCase()));
            }
            if (sector != null) {
                predicates.add(cb.equal(cb.lower(root.get("sector")), sector.toLowerCase()));
            }
            if (cursor != null) {
                predicates.add(cb.greaterThan(root.get("id"), cursor));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Asset> assets = assetRepository.findAll(
                spec, PageRequest.of(0, limit + 1, Sort.by("id").ascending())).getContent();

        boolean hasMore = assets.size() > limit;
        if (hasMore) {
            assets = assets.subList(0, limit);
        }

        List<AssetResponse> data = assets.stream()
                .map(EntityMapper::toAssetResponse)
                .toList();

        String nextCursor = hasMore ? assets.get(assets.size() - 1).getId() : null;

        return PaginatedResponse.of(data, hasMore, nextCursor, null);
    }

    public AssetResponse getAsset(String assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> ApiException.notFound("asset"));
        return EntityMapper.toAssetResponse(asset);
    }
}
