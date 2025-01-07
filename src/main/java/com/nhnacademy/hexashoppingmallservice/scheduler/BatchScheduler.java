package com.nhnacademy.hexashoppingmallservice.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@Profile("prod")
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job dormantMemberJob;

    public BatchScheduler(JobLauncher jobLauncher, Job dormantMemberJob) {
        this.jobLauncher = jobLauncher;
        this.dormantMemberJob = dormantMemberJob;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void runDormantMemberJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(dormantMemberJob, jobParameters);
        } catch (Exception e) {
            // 로깅 및 예외 처리
            log.error(e.getMessage(), e);
        }
    }
}
