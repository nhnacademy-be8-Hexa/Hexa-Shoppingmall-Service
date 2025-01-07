package com.nhnacademy.hexashoppingmallservice.service.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Delivery;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.DeliveryNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.DeliveryProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.DeliveryRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import com.nhnacademy.hexashoppingmallservice.service.order.DeliveryService;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DeliveryServiceTest {
    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Order order;
    private DeliveryRequestDTO deliveryRequestDTO;
    private Delivery delivery;
    private Pageable pageable;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Member member = mock(Member.class);
        OrderStatus orderStatus = mock(OrderStatus.class);
        WrappingPaper wrappingPaper = mock(WrappingPaper.class);

        order = Order.of(member, 10000, wrappingPaper, orderStatus, "12345", "address", "addressDetail");

        Field authorIdField = order.getClass().getDeclaredField("orderId");
        authorIdField.setAccessible(true);
        authorIdField.set(order, 1L);

        deliveryRequestDTO = new DeliveryRequestDTO(1L, 2, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        delivery = Delivery.of(order, deliveryRequestDTO.getDeliveryAmount(), deliveryRequestDTO.getDeliveryDate(), deliveryRequestDTO.getDeliveryReleaseDate() );
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createDelivery_OrderNotFound() {
        when(orderRepository.findById(deliveryRequestDTO.getOrderId())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () ->
                deliveryService.createDelivery(deliveryRequestDTO));

        verify(orderRepository, times(1)).findById(deliveryRequestDTO.getOrderId());
    }

    @Test
    void createDelivery_success() {
        when(orderRepository.findById(deliveryRequestDTO.getOrderId())).thenReturn(Optional.of(order));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        Delivery createdDelivery = deliveryService.createDelivery(deliveryRequestDTO);

        assertNotNull(createdDelivery);
        assertEquals(order, createdDelivery.getOrder());
        assertEquals(deliveryRequestDTO.getDeliveryAmount(), createdDelivery.getDeliveryAmount());
    }

    @Test
    void getDeliveries() {
        DeliveryProjection deliveryProjection = mock(DeliveryProjection.class);
        Page<DeliveryProjection> page = new PageImpl<>(List.of(deliveryProjection));
        when(deliveryRepository.findAllBy(pageable)).thenReturn(page);

        List<DeliveryProjection> result = deliveryService.getDeliveries(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryRepository, times(1)).findAllBy(pageable);
    }

    @Test
    void getDeliveryByOrderId() {
        Long orderId = 1L;
        DeliveryProjection deliveryProjection = mock(DeliveryProjection.class);
        DeliveryProjection.OrderProjection order = mock(DeliveryProjection.OrderProjection.class);

        when(order.getOrderId()).thenReturn(orderId);
        when(deliveryProjection.getOrder()).thenReturn(order);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(deliveryProjection));

        DeliveryProjection result = deliveryService.getDeliveryByOrderId(orderId);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrder().getOrderId());

        when(deliveryRepository.findByOrderId(order.getOrderId())).thenReturn(Optional.empty());

        Assertions.assertThrows(DeliveryNotFoundException.class, () ->
                deliveryService.getDeliveryByOrderId(order.getOrderId()));
    }

    @Test
    void getDeliveriesByMemberId() {
        Member member = mock(Member.class);
        DeliveryProjection deliveryProjection = mock(DeliveryProjection.class);

        when(memberRepository.findById("member1")).thenReturn(Optional.of(member));
        Page<DeliveryProjection> page = new PageImpl<>(List.of(deliveryProjection));
        when(deliveryRepository.findAllByOrder_Member(member, pageable)).thenReturn(page);

        List<DeliveryProjection> result = deliveryService.getDeliveriesByMemberId("member1", pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryRepository, times(1)).findAllByOrder_Member(member, pageable);

        when(memberRepository.findById("member1")).thenReturn(Optional.empty());

        Assertions.assertThrows(MemberNotFoundException.class, () ->
                deliveryService.getDeliveriesByMemberId("member1", pageable));
    }

    @Test
    void updateDelivery() {
        delivery.setDeliveryDate(deliveryRequestDTO.getDeliveryDate());
        delivery.setDeliveryReleaseDate(deliveryRequestDTO.getDeliveryReleaseDate());

        DeliveryRequestDTO updateRequestDTO = new DeliveryRequestDTO(
                1L, 5, LocalDateTime.now(), LocalDateTime.now().plusDays(5));

        when(deliveryRepository.findById(order.getOrderId())).thenReturn(Optional.of(delivery));

        deliveryService.updateDelivery(order.getOrderId(), updateRequestDTO);

        assertTrue(updateRequestDTO.getDeliveryDate().isEqual(delivery.getDeliveryDate()));
        assertTrue(updateRequestDTO.getDeliveryReleaseDate().isEqual(delivery.getDeliveryReleaseDate()));

        when(deliveryRepository.findById(order.getOrderId())).thenReturn(Optional.empty());

        Assertions.assertThrows(DeliveryNotFoundException.class, () ->
                deliveryService.updateDelivery(order.getOrderId(), updateRequestDTO));

    }


}