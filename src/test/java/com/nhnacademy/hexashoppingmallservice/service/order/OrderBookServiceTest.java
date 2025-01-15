package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.book.OrderBookDTO;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderBookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.querydsl.impl.OrderBookRepositoryCustomImpl;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderBookServiceTest {

    @Mock
    private OrderBookRepository orderBookRepository;

    @Mock
    private OrderBookRepositoryCustomImpl orderBookRepositoryCustom;

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

    @Test
    void testGetOrderBooksByOrderId_whenOrderFound_shouldReturnOrderBooks() {
        // Given
        Long orderId = 123L;

        // 새로운 OrderBookDTO 객체 생성
        OrderBookDTO orderBookDTO = new OrderBookDTO(
                123L,  // orderId
                456L,  // bookId
                "The Great Book",  // bookTitle
                2,  // orderBookAmount
                5000,  // bookPrice
                789L   // couponId
        );

        List<OrderBookDTO> expectedOrderBooks = Collections.singletonList(orderBookDTO);

        when(orderBookRepository.existsByOrder_OrderId(orderId)).thenReturn(true);
        when(orderBookRepositoryCustom.findOrderBooksByOrderId(orderId)).thenReturn(expectedOrderBooks);

        // When
        List<OrderBookDTO> actualOrderBooks = orderBookService.getOrderBooksByOrderId(orderId);

        // Then
        assertNotNull(actualOrderBooks);
        assertEquals(1, actualOrderBooks.size());

        OrderBookDTO actualOrderBook = actualOrderBooks.get(0);
        assertEquals(123L, actualOrderBook.getOrderId());
        assertEquals(456L, actualOrderBook.getBookId());
        assertEquals("The Great Book", actualOrderBook.getBookTitle());
        assertEquals(2, actualOrderBook.getOrderBookAmount());
        assertEquals(5000, actualOrderBook.getBookPrice());
        assertEquals(789L, actualOrderBook.getCouponId());
    }
}
