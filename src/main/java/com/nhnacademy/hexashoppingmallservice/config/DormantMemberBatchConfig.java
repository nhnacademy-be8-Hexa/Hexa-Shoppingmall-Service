package com.nhnacademy.hexashoppingmallservice.config;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
//하단 어노테이션은 수동 설정한 DormantMemberBatchConfig 설정과 충돌을 일으킴
//@EnableBatchProcessing
@Profile("prod")
@RequiredArgsConstructor
public class DormantMemberBatchConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final MemberService memberService;
    private final MemberStatusRepository memberStatusRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dormantMemberJob() {
        return new JobBuilder("dormantMemberJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dormantMemberStep())
                .build();
    }

    @Bean
    public Step dormantMemberStep() {
        return new StepBuilder("dormantMemberStep", jobRepository)
                .<Member, Member>chunk(100, transactionManager)
                .reader(memberItemReader())
                .processor(memberItemProcessor())
                .writer(memberItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Member> memberItemReader() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(3);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cutoffDate", cutoffDate);
        parameters.put("statusId", 1L); // 'ACTIVE' 상태의 statusId 값

        return new JpaPagingItemReaderBuilder<Member>()
                .name("memberItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT m FROM Member m WHERE m.memberLastLoginAt <= :cutoffDate AND m.memberStatus.statusId = :statusId")
                .parameterValues(parameters)
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<Member, Member> memberItemProcessor() {
        return member -> {
            // 상태를 'DORMANT'로 변경 (memberStatusId=2)
            MemberStatus dormantStatus = memberStatusRepository.findById(2L)
                    .orElseThrow(() -> new IllegalStateException("DORMANT status not found"));

            member.setMemberStatus(dormantStatus);
            log.info("Member " + member.getMemberId() + " status updated to DORMANT");
            return member;
        };
    }

    @Bean
    public ItemWriter<Member> memberItemWriter() {
        return chunk -> memberService.saveAll(chunk.getItems());
    }
}

