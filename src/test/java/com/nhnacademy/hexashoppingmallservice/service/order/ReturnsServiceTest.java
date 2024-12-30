package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.Returns;
import com.nhnacademy.hexashoppingmallservice.entity.order.ReturnsReason;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.ReturnsNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.ReturnsReasonNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.ReturnsReasonRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.ReturnsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ReturnsServiceTest {

    @Autowired
    private ReturnsService returnsService;

    @MockBean
    private ReturnsRepository returnsRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ReturnsReasonRepository returnsReasonRepository;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    void createReturns_success() {
        Long orderId = 1L;
        Long returnsReasonId = 1L;
        String returnsDetail = "Item damaged";

        ReturnsRequestDTO returnsRequestDTO = new ReturnsRequestDTO(orderId, returnsReasonId, returnsDetail);

        Order order = Mockito.mock(Order.class);
        ReturnsReason returnsReason = Mockito.mock(ReturnsReason.class);
        Returns returns = Returns.of(order, returnsReason, returnsDetail);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
        Mockito.when(returnsReasonRepository.findById(returnsReasonId)).thenReturn(java.util.Optional.of(returnsReason));
        Mockito.when(returnsRepository.save(Mockito.any(Returns.class))).thenReturn(returns);

        Returns createdReturns = returnsService.createReturns(returnsRequestDTO);

        assertThat(createdReturns).isNotNull();
        assertThat(createdReturns.getOrder()).isEqualTo(order);
        assertThat(createdReturns.getReturnsReason()).isEqualTo(returnsReason);
        assertThat(createdReturns.getReturnsDetail()).isEqualTo(returnsDetail);
    }

    @Test
    void createReturns_orderNotFound() {
        Long orderId = 1L;
        Long returnsReasonId = 1L;
        String returnsDetail = "Item damaged";

        ReturnsRequestDTO returnsRequestDTO = new ReturnsRequestDTO(orderId, returnsReasonId, returnsDetail);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> returnsService.createReturns(returnsRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("Order with id '1' not found");
    }

    @Test
    void createReturns_returnsReasonNotFound() {
        Long orderId = 1L;
        Long returnsReasonId = 1L;
        String returnsDetail = "Item damaged";

        ReturnsRequestDTO returnsRequestDTO = new ReturnsRequestDTO(orderId, returnsReasonId, returnsDetail);

        Order order = Mockito.mock(Order.class);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));
        Mockito.when(returnsReasonRepository.findById(returnsReasonId)).thenReturn(java.util.Optional.empty());

        ReturnsReasonNotFoundException exception = assertThrows(ReturnsReasonNotFoundException.class, () -> returnsService.createReturns(returnsRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("ReturnsReason with id '1' not found");
    }


    @Test
    void getReturns_success() {
        Pageable pageable = Mockito.mock(Pageable.class);
        List<Returns> returnsList = List.of(Mockito.mock(Returns.class), Mockito.mock(Returns.class));

        Mockito.when(returnsRepository.findAll(pageable)).thenReturn(new org.springframework.data.domain.PageImpl<>(returnsList));

        List<Returns> result = returnsService.getReturns(pageable);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(returnsList.size());
    }

    @Test
    void getReturns_emptyList() {
        Pageable pageable = Mockito.mock(Pageable.class);

        Mockito.when(returnsRepository.findAll(pageable)).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        List<Returns> result = returnsService.getReturns(pageable);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }


    @Test
    void getReturnsByOrderId_success() {
        Long orderId = 1L;
        Returns returns = Mockito.mock(Returns.class);

        Mockito.when(returnsRepository.findById(orderId)).thenReturn(java.util.Optional.of(returns));

        Returns result = returnsService.getReturnsByOrderId(orderId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(returns);
    }

    @Test
    void getReturnsByOrderId_returnsNotFound() {
        Long orderId = 1L;

        Mockito.when(returnsRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        ReturnsNotFoundException exception = assertThrows(ReturnsNotFoundException.class, () -> {
            returnsService.getReturnsByOrderId(orderId);
        });

        assertThat(exception.getMessage()).isEqualTo("Returns with id '1' not found");
    }


    @Test
    void getReturnsByReturnsReasonId_success() {
        Long returnsReasonId = 1L;
        Returns returns = Mockito.mock(Returns.class);

        Mockito.when(returnsRepository.findById(returnsReasonId)).thenReturn(java.util.Optional.of(returns));

        Returns result = returnsService.getReturnsByReturnsReasonId(returnsReasonId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(returns);
    }

    @Test
    void getReturnsByReturnsReasonId_returnsNotFound() {
        Long returnsReasonId = 1L;

        Mockito.when(returnsRepository.findById(returnsReasonId)).thenReturn(java.util.Optional.empty());

        ReturnsNotFoundException exception = assertThrows(ReturnsNotFoundException.class, () -> {
            returnsService.getReturnsByReturnsReasonId(returnsReasonId);
        });

        assertThat(exception.getMessage()).isEqualTo("Returns with id '1' not found");
    }


    @Test
    void getReturnsByMemberId_success() {
        String memberId = "member123";
        Member member = Mockito.mock(Member.class);
        Returns returns = Mockito.mock(Returns.class);

        Mockito.when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        Mockito.when(returnsRepository.findByOrder_Member(member)).thenReturn(java.util.Optional.of(returns));

        Returns result = returnsService.getReturnsByMemberId(memberId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(returns);
    }

    @Test
    void getReturnsByMemberId_memberNotFound() {
        String memberId = "member123";

        Mockito.when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.empty());

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> returnsService.getReturnsByMemberId(memberId));

        assertThat(exception.getMessage()).isEqualTo("Member with id 'member123' not found");
    }

    @Test
    void test_getReturnsByMemberId_OrderNotFound() throws Exception {
        // given: Member와 연관된 Returns(Order)가 없는 상황 설정
        String memberId = "testMember";
        Member member = Member.builder().build();
        Field memberIdField = member.getClass().getDeclaredField("memberId");
        memberIdField.setAccessible(true);
        memberIdField.set(member, memberId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(returnsRepository.findByOrder_Member(member)).willReturn(Optional.empty());

        // then: OrderNotFoundException 발생 검증
        assertThrows(OrderNotFoundException.class, () -> {
            returnsService.getReturnsByMemberId(memberId);
        });
    }



    @Test
    void updateReturns_success() {
        Long orderId = 1L;
        String updatedDetail = "Updated item description";
        ReturnsRequestDTO returnsRequestDTO = new ReturnsRequestDTO(orderId, null, updatedDetail);

        Returns existingReturns = Mockito.mock(Returns.class);
        Mockito.when(returnsRepository.findById(orderId)).thenReturn(java.util.Optional.of(existingReturns));
        Mockito.when(returnsRepository.save(existingReturns)).thenReturn(existingReturns);

        Returns updatedReturns = returnsService.updateReturns(orderId, returnsRequestDTO);

        assertThat(updatedReturns).isNotNull();
        assertThat(updatedReturns).isEqualTo(existingReturns);
        Mockito.verify(existingReturns).setReturnsDetail(updatedDetail);
    }

    @Test
    void updateReturns_returnsNotFound() {
        Long orderId = 1L;
        String updatedDetail = "Updated item description";
        ReturnsRequestDTO returnsRequestDTO = new ReturnsRequestDTO(orderId, null, updatedDetail);

        Mockito.when(returnsRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        ReturnsNotFoundException exception = assertThrows(ReturnsNotFoundException.class, () -> {
            returnsService.updateReturns(orderId, returnsRequestDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Returns not found for orderId: " + orderId);
    }


    @Test
    void deleteReturns_success() {
        Long orderId = 1L;
        Returns existingReturns = Mockito.mock(Returns.class);

        Mockito.when(returnsRepository.findById(orderId)).thenReturn(java.util.Optional.of(existingReturns));
        Mockito.doNothing().when(returnsRepository).deleteById(orderId);

        returnsService.deleteReturns(orderId);

        Mockito.verify(returnsRepository).deleteById(orderId);
    }

    @Test
    void deleteReturns_returnsNotFound() {
        Long orderId = 1L;

        Mockito.when(returnsRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        ReturnsNotFoundException exception = assertThrows(ReturnsNotFoundException.class, () -> {
            returnsService.deleteReturns(orderId);
        });

        assertThat(exception.getMessage()).isEqualTo("Returns not found for orderId: " + orderId);
    }
}