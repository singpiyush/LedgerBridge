package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.Ingestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionRepository extends JpaRepository<Ingestion, String> {
}
