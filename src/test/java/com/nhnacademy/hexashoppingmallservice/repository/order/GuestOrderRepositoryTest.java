package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.GuestOrder;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.projection.order.GuestOrderProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
class GuestOrderRepositoryTest {
    @Autowired
    private GuestOrderRepository guestOrderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private GuestOrder guestOrder;

    @BeforeEach
    void setUp() {

        WrappingPaper wrappingPaper = WrappingPaper.builder().wrappingPaperName("Basic").wrappingPaperPrice(1000).build();
        OrderStatus orderStatus = OrderStatus.builder().orderStatus("DELIVERED").build();
        entityManager.persist(wrappingPaper);
        entityManager.persist(orderStatus);

        Rating rating = Rating.of("Gold", 90);
        MemberStatus memberStatus = MemberStatus.of("Active");
        entityManager.persist(rating);
        entityManager.persist(memberStatus);

        Member member = createMember(rating, memberStatus);
        Order order = createOrder(member,wrappingPaper,orderStatus,LocalDateTime.now());

        this.guestOrder = createGuestOrder("test123", "123", "test@naver.com", order);

        // Persist test objects
        entityManager.persist(member);
        entityManager.persist(order);
        entityManager.persist(guestOrder);
        entityManager.flush();
    }

    @Test
    void testFindByOrderId() {
        GuestOrderProjection projection = guestOrderRepository.findByOrderId(guestOrder.getOrderId());

        assertNotNull(projection, "Projection should not be null");
        assertEquals(guestOrder.getOrderId(), projection.getOrderId(), "Order IDs should match");
        assertEquals(guestOrder.getGuestOrderEmail(), projection.getGuestOrderEmail(), "Emails should match");
    }

    @Test
    void testFindAllBy_Pagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GuestOrderProjection> result = guestOrderRepository.findAllBy(pageable);

        assertNotNull(result, "Result page should not be null");
        assertEquals(1, result.getTotalElements(), "Total elements should match the number of saved orders");
        assertEquals(1, result.getContent().size(), "Page content size should match");

        GuestOrderProjection projection = result.getContent().get(0);
        assertEquals(guestOrder.getOrderId(), projection.getOrderId(), "Order IDs should match");
        assertEquals(guestOrder.getGuestOrderEmail(), projection.getGuestOrderEmail(), "Emails should match");
    }

    private Member createMember(Rating rating, MemberStatus memberStatus) {


        return Member.of(
                "member123", "password123", "홍길동", "01012345678",
                "test@naver.com", LocalDate.of(1990, 1, 1), rating, memberStatus
        );
    }

    private Order createOrder(Member member , WrappingPaper wrappingPaper, OrderStatus orderStatus, LocalDateTime orderedAt ) {


        return Order.builder()
                .member(member)
                .orderPrice(20000)
                .orderedAt(LocalDateTime.now())
                .wrappingPaper(wrappingPaper)
                .orderStatus(orderStatus)
                .zoneCode("10010")
                .address("서울특별시 강남구 역삼동")
                .addressDetail("101동 101호")
                .build();
    }

    private GuestOrder createGuestOrder(String orderNumber, String password, String email, Order order) {
        return GuestOrder.builder()
                .guestOrderNumber(orderNumber)
                .guestOrderPassword(password)
                .guestOrderEmail(email)
                .order(order)
                .build();
    }
}