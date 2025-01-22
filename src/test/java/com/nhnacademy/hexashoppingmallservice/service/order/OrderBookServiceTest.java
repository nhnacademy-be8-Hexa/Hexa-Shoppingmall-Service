package com.nhnacademy.hexashoppingmallservice.service.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nhnacademy.hexashoppingmallservice.dto.book.OrderBookDTO;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderBookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderBookRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @Test
    void testGetOrderBooksByOrderId_whenOrderFound_shouldReturnOrderBooks() {
        // Given
        Long orderId = 123L;
        Long bookId = 456L;
        String bookTitle = "Sample Book Title";
        Integer orderBookAmount = 3;
        Integer bookPrice = 10000;
        Long couponId = 789L;

        // OrderBookDTO 객체 생성
        OrderBookDTO orderBookDTO = new OrderBookDTO(
                orderId, bookId, bookTitle, orderBookAmount, bookPrice, couponId
        );
        List<OrderBookDTO> expectedOrderBooks = Collections.singletonList(orderBookDTO);

        // Mocking repository methods
        when(orderBookRepository.existsByOrder_OrderId(orderId)).thenReturn(true);
        when(orderBookRepository.findOrderBooksByOrderId(orderId)).thenReturn(expectedOrderBooks);

        // When
        List<OrderBookDTO> actualOrderBooks = orderBookService.getOrderBooksByOrderId(orderId);

        // Then
        assertNotNull(actualOrderBooks);
        assertEquals(1, actualOrderBooks.size());
        OrderBookDTO actualOrderBookDTO = actualOrderBooks.get(0);
        assertEquals(orderId, actualOrderBookDTO.getOrderId());
        assertEquals(bookId, actualOrderBookDTO.getBookId());
        assertEquals(bookTitle, actualOrderBookDTO.getBookTitle());
        assertEquals(orderBookAmount, actualOrderBookDTO.getOrderBookAmount());
        assertEquals(bookPrice, actualOrderBookDTO.getBookPrice());
        assertEquals(couponId, actualOrderBookDTO.getCouponId());
    }

}
