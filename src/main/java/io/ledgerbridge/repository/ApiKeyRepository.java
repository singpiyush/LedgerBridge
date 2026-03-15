package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {
    Optional<ApiKey> findByKeyHashAndActiveTrue(String keyHash);
    Optional<ApiKey> findByKeyPrefixAndActiveTrue(String keyPrefix);
}
