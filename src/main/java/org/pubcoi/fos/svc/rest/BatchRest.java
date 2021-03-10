package org.pubcoi.fos.svc.rest;

import org.pubcoi.fos.svc.models.dao.BatchJobRecentExecDAO;
import org.pubcoi.fos.svc.models.dao.BatchJobStepDAO;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Profile("batch")
@RestController
public class BatchRest {

    final JobExplorer jobExplorer;

    public BatchRest(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    @GetMapping("/api/batch/jobs/executions")
    public List<BatchJobRecentExecDAO> execution(
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
        ArrayList<BatchJobRecentExecDAO> recentExecutions = new ArrayList<>();
        return jobExplorer.findJobInstancesByJobName("process_attachment", start, 10).stream()
                .map(instance -> jobExplorer.getJobExecution(instance.getInstanceId()))
                .filter(Objects::nonNull)
                .map(job -> {
                    BatchJobRecentExecDAO execution = new BatchJobRecentExecDAO()
                            .setJobId(job.getJobId())
                            .setAttachmentId(job.getJobParameters().getString("attachment_id"))
                            .setStatus(job.getStatus())
                            .setStartTime(OffsetDateTime.ofInstant(job.getStartTime().toInstant(), ZoneOffset.UTC))
                            .setEndTime(null == job.getEndTime() ? null : // contract says otherwise, but can in fact be null
                                    OffsetDateTime.ofInstant(job.getEndTime().toInstant(), ZoneOffset.UTC)
                            );
                    for (StepExecution stepExecution : job.getStepExecutions()) {
                        execution.getSteps().add(new BatchJobStepDAO()
                                .setId(stepExecution.getId())
                                .setStepName(stepExecution.getStepName())
                                .setStartTime(OffsetDateTime.ofInstant(stepExecution.getStartTime().toInstant(), ZoneOffset.UTC))
                                .setEndTime(null == job.getEndTime() ? null :
                                        OffsetDateTime.ofInstant(stepExecution.getEndTime().toInstant(), ZoneOffset.UTC)
                                )
                                .setStatus(stepExecution.getStatus())
                        );
                    }
                    return execution;
                })
                .collect(Collectors.toList());
    }

}
