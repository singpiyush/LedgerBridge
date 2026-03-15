package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRepository extends JpaRepository<Transaction, String>,
        JpaSpecificationExecutor<Transaction> {
}
