/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

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
                            .setEndTime(null == job.getEndTime() ? null : // API contract suggests otherwise, but can in fact be null
                                    OffsetDateTime.ofInstant(job.getEndTime().toInstant(), ZoneOffset.UTC)
                            );
                    for (StepExecution stepExecution : job.getStepExecutions()) {
                        execution.getSteps().add(new BatchJobStepDAO()
                                .setId(stepExecution.getId())
                                .setStepName(stepExecution.getStepName())
                                .setStartTime(OffsetDateTime.ofInstant(stepExecution.getStartTime().toInstant(), ZoneOffset.UTC))
                                .setEndTime(null == job.getEndTime() ? null : // API contract suggests otherwise, but can in fact be null
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
