package com.nhnacademy.hexashoppingmallservice.config;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberOrderSummary3M;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
//하단 어노테이션은 수동 설정한 DormantMemberBatchConfig 설정과 충돌을 일으킴
//@EnableBatchProcessing
@Profile("prod")
@RequiredArgsConstructor
public class MemberBatchConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final MemberService memberService;
    private final MemberStatusRepository memberStatusRepository;
    private final PlatformTransactionManager transactionManager;
    private final MemberOrderSummary3MJobListener jobListener;//// 테이블 초기화 리스너

    @Bean
    @Transactional
    public Job MemberJob() {
        return new JobBuilder("MemberJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(dormantMemberStep())
                .next(orderSummaryStep())
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
                .queryString("SELECT m FROM Member m JOIN FETCH m.memberStatus WHERE m.memberLastLoginAt <= :cutoffDate AND m.memberStatus.statusId = :statusId")
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
            log.info("Member {} status updated to DORMANT", member.getMemberId());
            return member;
        };
    }

    @Bean
    public ItemWriter<Member> memberItemWriter() {
        return chunk -> memberService.saveAll(chunk.getItems());
    }




    @Bean
    public Step orderSummaryStep() {
        return  new StepBuilder("orderSummaryStep", jobRepository)
                .<Order,MemberOrderSummary3M>chunk(100,transactionManager)
                .reader(orderItemReader())  // Order 데이터 읽어오기
                .processor(orderItemProcessor())  // 데이터 처리 (합산)
                .writer(memberOrderSummary3MItemWriter())  // 합산 후 DB에 저장
                .build();
    }

    // 2. ItemReader: Order 테이블에서 최근 3개월치, member가 null 아닌 데이터 읽기
//    @Bean
//    public JpaPagingItemReader<Order> orderItemReader() {
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("threeMonthsAgo", LocalDateTime.now().minusMonths(3));
//
//        return new JpaPagingItemReaderBuilder<Order>()
//                .name("orderItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("SELECT o FROM Order o WHERE o.orderedAt >= :threeMonthsAgo AND o.member IS NOT NULL")
//                .parameterValues(parameters)
//                .pageSize(100)
//                .build();
//    }

    @Bean
    public JpaPagingItemReader<Order> orderItemReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("threeMonthsAgo", LocalDateTime.now().minusMonths(3));
        parameters.put("orderStatus", "COMPLETE");  // "COMPLETE" 상태를 필터링하는 파라미터 추가

        return new JpaPagingItemReaderBuilder<Order>()
                .name("orderItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT o FROM Order o WHERE o.orderedAt >= :threeMonthsAgo " +
                        "AND o.member IS NOT NULL AND o.orderStatus.orderStatus = :orderStatus")  // orderStatus 필드에 대한 조건 추가
                .parameterValues(parameters)
                .pageSize(100)
                .build();
    }

    // 3. ItemProcessor: Order의 price 합산하여 MemberOrderSummary3M으로 변환
    @Bean
    public ItemProcessor<Order, MemberOrderSummary3M> orderItemProcessor() {
        return new ItemProcessor<Order, MemberOrderSummary3M>() {
            private Map<String, MemberOrderSummary3M> summaryMap;

            @BeforeStep
            public void beforeStep(StepExecution stepExecution) {
                summaryMap = new HashMap<>();
            }

            @Override
            public MemberOrderSummary3M process(Order order) throws Exception {
                String memberId = order.getMember().getMemberId();
                Integer orderPrice = order.getOrderPrice();

                MemberOrderSummary3M summary = summaryMap.getOrDefault(memberId, MemberOrderSummary3M.of(memberId, 0));
                summary.setTotalOrderPrice(summary.getTotalOrderPrice() + orderPrice);
                summaryMap.put(memberId, summary);

                return summary; // 변경된 summary 반환
            }
        };
    }

    @Bean
    public ItemWriter<MemberOrderSummary3M> memberOrderSummary3MItemWriter() {
        JpaItemWriter<MemberOrderSummary3M> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

}

