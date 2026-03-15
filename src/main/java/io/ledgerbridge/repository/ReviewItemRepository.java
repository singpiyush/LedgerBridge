package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.ReviewItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewItemRepository extends JpaRepository<ReviewItem, String> {
    List<ReviewItem> findByIngestionIdAndResolvedFalse(String ingestionId);
    List<ReviewItem> findByIngestionId(String ingestionId);
}
