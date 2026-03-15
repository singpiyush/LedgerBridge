package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.Connection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, String> {
    List<Connection> findByUserIdAndIdGreaterThanOrderByIdAsc(String userId, String cursor, Pageable pageable);
    List<Connection> findByUserIdOrderByIdAsc(String userId, Pageable pageable);
    Optional<Connection> findByIdAndUserId(String id, String userId);
    long countByUserId(String userId);
}
