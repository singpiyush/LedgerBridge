package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, String>,
        JpaSpecificationExecutor<Holding> {
    List<Holding> findByUserId(String userId);
}
