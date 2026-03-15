package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.JobResponse;
import io.ledgerbridge.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/{job_id}")
    public ResponseEntity<JobResponse> getJob(
            @PathVariable("job_id") String jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }
}
