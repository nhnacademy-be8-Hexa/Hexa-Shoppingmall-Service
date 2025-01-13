package com.nhnacademy.hexashoppingmallservice.config;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberOrderSummary3M;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberOrderSummary3MRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import com.nhnacademy.hexashoppingmallservice.service.member.RatingService;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

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
public class MemberBatchConfig {

    final String RATING_NORMAL = "Normal";
    final String RATING_ROYAL = "Royal";
    final String RATING_Gold = "Gold";
    final String RATING_PLATINUM = "Platinum";

    final Long ROYAL_AMOUNT = 100000L;
    final Long GOLD_AMOUNT = 200000L;
    final Long PLATINUM_AMOUNT = 300000L;


    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final MemberService memberService;
    private final MemberStatusRepository memberStatusRepository;
    private final PlatformTransactionManager transactionManager;
    private final MemberOrderSummary3MJobListener jobListener;
    private final MemberRepository memberRepository;
    private final RatingService ratingService;
    private final RatingRepository ratingRepository;
    private final MemberOrderSummary3MRepository memberOrderSummary3MRepository;

    //// 테이블 초기화 리스너

    @Bean
    @Transactional
    public Job MemberJob() {
        return new JobBuilder("MemberJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(dormantMemberStep())
                .next(orderSummaryStep())
                .next(resetMemberGradeStep())
                .next(recalculateMemberGradeStep())
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






    // 자정마다 주문 합계 조회하는 스텝

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




    // 자정마다 멤버 등급 초기화 함수

    @Bean
    public Step resetMemberGradeStep() {

        return new StepBuilder("updateMemberGradeStep",jobRepository)
                .<Member, Member>chunk(100,transactionManager)
                .reader(memberItemReader())  // 멤버 데이터를 읽어오는 리더
                .processor(updateGradeProcessor())  // 등급을 업데이트하는 프로세서
                .writer(updateGradeWriter())  // 업데이트된 등급을 반영하는 라이터
                .build();
    }



    private ItemReader<Member> resetMemberRatingItemReader() {
        List<Member> members = memberRepository.findAll();
        return new ListItemReader<>(members);
    }

    private ItemProcessor<Member, Member> updateGradeProcessor() {

        return new ItemProcessor<Member, Member>() {

            private final Rating rating = ratingRepository.findByRatingName(RATING_NORMAL);

            @Override
            public Member process(Member item) throws Exception {
                item.setRating(rating);
                return item;
            }

        };

    }

    private ItemWriter<Member> updateGradeWriter() {
        // 멤버들의 등급을 DB에 업데이트
        return memberRepository::saveAll;
    }





    // 금액에 따른 등급 재조정 스텝

    @Bean
    public Step recalculateMemberGradeStep() {

        return new StepBuilder("recalculateMemberGradeStep", jobRepository)
                .<MemberOrderSummary3M, Member>chunk(100, transactionManager)
                .reader(orderSummaryItemReader())
                .processor(recalculateGradeProcessor())  // 총 주문금액을 기반으로 등급을 다시 매기는 프로세서
                .writer(recalculateGradeWriter())  // 새로 매겨진 등급을 업데이트하는 라이터
                .build();
    }




    private ItemReader<MemberOrderSummary3M> orderSummaryItemReader() {
        List<MemberOrderSummary3M> summaries = memberOrderSummary3MRepository.findAll();
        return new ListItemReader<>(summaries);
    }

    private ItemProcessor<MemberOrderSummary3M, Member> recalculateGradeProcessor() {

        Rating royal = ratingRepository.findByRatingName(RATING_ROYAL);
        Rating gold = ratingRepository.findByRatingName(RATING_Gold);
        Rating platimum = ratingRepository.findByRatingName(RATING_PLATINUM);



        return orderSummary -> {
            // 3개월치 주문 금액을 기반으로 등급을 다시 매긴다
            Member member = memberRepository.findById(orderSummary.getMemberId()).orElseThrow();
            int totalOrderPrice = orderSummary.getTotalOrderPrice();

            if(totalOrderPrice>=ROYAL_AMOUNT){
                member.setRating(royal);
            }
            if (totalOrderPrice>=GOLD_AMOUNT) {
                member.setRating(gold);
            }
            if (totalOrderPrice>=PLATINUM_AMOUNT) {
                member.setRating(platimum);
            }
            return member;
        };
    }

    private ItemWriter<Member> recalculateGradeWriter() {
        // 새로 매겨진 등급을 DB에 업데이트
        return memberRepository::saveAll;
    }


}

