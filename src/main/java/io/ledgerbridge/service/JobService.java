package io.ledgerbridge.service;

import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.JobResponse;
import io.ledgerbridge.model.entity.Job;
import io.ledgerbridge.repository.JobRepository;
import io.ledgerbridge.util.EntityMapper;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobResponse getJob(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> ApiException.notFound("job"));
        return EntityMapper.toJobResponse(job);
    }
}
