package com.adhd.ad_hell.domain.user.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteStausUserJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteStatusUserBatchJob;

    @Scheduled(cron = "0 0 0 * * *")
    public void runDeleteStausUserJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("timetamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(deleteStatusUserBatchJob, params);
    }
}
