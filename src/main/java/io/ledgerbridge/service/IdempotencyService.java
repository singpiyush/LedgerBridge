package io.ledgerbridge.service;

import io.ledgerbridge.model.entity.IdempotencyRecord;
import io.ledgerbridge.repository.IdempotencyRecordRepository;
import io.ledgerbridge.util.RequestContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;

    public IdempotencyService(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    public Optional<IdempotencyRecord> findExisting(String idempotencyKey) {
        if (idempotencyKey == null) {
            return Optional.empty();
        }
        String accountId = RequestContext.getAccountId();
        return repository.findByIdempotencyKeyAndAccountId(idempotencyKey, accountId)
                .filter(record -> record.getExpiresAt().isAfter(Instant.now()));
    }

    @Transactional
    public void save(String idempotencyKey, int status, String responseBody) {
        if (idempotencyKey == null) {
            return;
        }
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(idempotencyKey);
        record.setAccountId(RequestContext.getAccountId());
        record.setResponseStatus(status);
        record.setResponseBody(responseBody);
        repository.save(record);
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    @Transactional
    public void cleanupExpired() {
        repository.deleteExpired(Instant.now());
    }
}
