package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.WebhookSubscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, String> {
    List<WebhookSubscription> findByAccountIdOrderByIdAsc(String accountId, Pageable pageable);
    List<WebhookSubscription> findByAccountIdAndIdGreaterThanOrderByIdAsc(String accountId, String cursor, Pageable pageable);
    List<WebhookSubscription> findByEnabledTrue();
    long countByAccountId(String accountId);
}
