package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.exception.order.OrderBookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class OrderBookServiceTest {

    @Mock
    private OrderBookRepository orderBookRepository;

    @InjectMocks
    private OrderBookService orderBookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrderBooksByOrderId_whenOrderNotFound_shouldThrowException() {
        // Given
        Long orderId = 123L;
        when(orderBookRepository.existsByOrder_OrderId(orderId)).thenReturn(false);

        // When & Then
        OrderBookNotFoundException exception = assertThrows(OrderBookNotFoundException.class, () -> {
            orderBookService.getOrderBooksByOrderId(orderId);
        });

        assertEquals("주문 ID 123에 해당하는 주문이 존재하지 않습니다.", exception.getMessage());
    }

}
