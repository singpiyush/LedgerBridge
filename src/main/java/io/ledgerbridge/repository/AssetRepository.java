package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, String>,
        JpaSpecificationExecutor<Asset> {
    Optional<Asset> findBySymbolAndExchange(String symbol, String exchange);
    Optional<Asset> findByIsin(String isin);
}
