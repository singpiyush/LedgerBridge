package io.ledgerbridge.repository;

import io.ledgerbridge.model.entity.Job;
import io.ledgerbridge.model.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findByStatusIn(List<JobStatus> statuses);
}
