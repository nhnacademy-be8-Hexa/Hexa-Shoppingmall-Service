package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.*;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReturnsRepositoryTest {

    @Autowired
    private ReturnsRepository returnsRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private MemberStatus memberStatus;
    private Rating rating;
    private Member member;
    private WrappingPaper wrappingPaper;
    private OrderStatus orderStatus;
    private Order order;
    private Returns returns;
    private ReturnsReason returnsReason;

    @BeforeEach
    void setUp() throws Exception {
        // 멤버 관련
        rating = Rating.of("Gold", 90);
        memberStatus = MemberStatus.of("Active");
        entityManager.persist(rating);
        entityManager.persist(memberStatus);

        member = createMember(rating, memberStatus);
        entityManager.persist(member);

        // 주문관련
        wrappingPaper = WrappingPaper.builder().wrappingPaperName("Basic").wrappingPaperPrice(1000).build();
        orderStatus = OrderStatus.builder().orderStatus("DELIVERED").build();
        entityManager.persist(wrappingPaper);
        entityManager.persist(orderStatus);

        order = createOrder(member, wrappingPaper, orderStatus, LocalDateTime.now());
        entityManager.persist(order);

        // 실제로 데이터베이스에 저장
        entityManager.flush();
    }

    @Test
    @DisplayName("findByOrder_Member: 특정 회원과 연결된 반품 조회")
    void findByOrder_Member() {
        // Given
        ReturnsReason reason = ReturnsReason.builder()
                .returnsReason("no-reason")
                .build();
        entityManager.persist(reason);

        // Returns 엔티티 생성
        Returns returns = Returns.builder()
                .returnsDetail("no reason")
                .returnsReason(reason)
                .order(order) // Order 객체 연결
                .build();

        // Returns 엔티티 저장
        returnsRepository.save(returns);

        // Returns 객체 저장 후, Order ID가 자동으로 할당됨
        Long orderId = returns.getOrder().getOrderId();

        // When
        Optional<Returns> result = returnsRepository.findByOrder_Member(member);

        // Then
        assertThat(result).isPresent(); // 반품 데이터가 존재하는지 확인
        assertThat(result.get().getOrder().getMember()).isEqualTo(member); // 멤버가 동일한지 확인
        assertThat(orderId).isNotNull();  // Order ID가 제대로 할당되었는지 확인
    }

    private Member createMember(Rating rating, MemberStatus memberStatus) {
        return Member.of(
                "member123", "password123", "홍길동", "01012345678",
                "test@naver.com", LocalDate.of(1990, 1, 1), rating, memberStatus
        );
    }

    private Order createOrder(Member member, WrappingPaper wrappingPaper, OrderStatus orderStatus, LocalDateTime orderedAt) {
        return Order.builder()
                .member(member)
                .orderPrice(20000)
                .orderedAt(orderedAt)
                .wrappingPaper(wrappingPaper)
                .orderStatus(orderStatus)
                .zoneCode("10010")
                .address("서울특별시 강남구 역삼동")
                .addressDetail("101동 101호")
                .build();
    }
}
