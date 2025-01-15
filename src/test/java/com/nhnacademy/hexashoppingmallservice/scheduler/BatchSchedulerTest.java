package com.nhnacademy.hexashoppingmallservice.scheduler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.mockito.Mockito.*;

@EnableScheduling
@ExtendWith(MockitoExtension.class)  // Mockito 확장 사용
class BatchSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job memberJob;

    @InjectMocks
    private BatchScheduler batchScheduler;

    @BeforeEach
    void setUp() {
        // 필요한 초기화 작업이 있으면 작성
    }

    @Test
    void runMemberJob_shouldRunJobSuccessfully() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // when
        batchScheduler.runMemberJob();

        // then
        verify(jobLauncher, times(1)).run(eq(memberJob), any(JobParameters.class));
    }

    @Test
    void runMemberJob_shouldHandleJobExecutionException() throws Exception {
        // given
        doAnswer(invocation -> {
            throw new JobExecutionException("Job execution failed");
        }).when(jobLauncher).run(any(Job.class), any(JobParameters.class));

        // when
        batchScheduler.runMemberJob();

        // then
        // 예외 발생을 로깅에서 확인하거나 예외 처리된 상태 확인
        verify(jobLauncher, times(1)).run(any(Job.class), any(JobParameters.class));
    }
}
