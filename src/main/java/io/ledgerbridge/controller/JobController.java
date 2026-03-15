package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.JobResponse;
import io.ledgerbridge.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/jobs")
@Tag(name = "Jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(
            summary = "Get async job status",
            description = """
                    Poll this endpoint to track the progress of asynchronous operations:

                    - **email_scan** — Scanning connected email for financial documents
                    - **document_parse** — Extracting transactions from uploaded documents
                    - **portfolio_recompute** — Recalculating holdings and gains after new data

                    The `progress` field (0-100) indicates completion percentage.
                    When `status` is `completed`, the `result` field contains operation-specific data.

                    **Tip:** Use webhooks instead of polling for production integrations.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job status",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
            @ApiResponse(responseCode = "404", description = "Job not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{job_id}")
    public ResponseEntity<JobResponse> getJob(
            @Parameter(description = "Job ID", example = "job_x1y2z3", required = true)
            @PathVariable("job_id") String jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }
}
