package com.nhnacademy.hexashoppingmallservice.util;

import static org.junit.jupiter.api.Assertions.*;

import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderCheckUtilTest {

    private OrderCheckUtil orderCheckUtil;

    @BeforeEach
    void setUp() {
        orderCheckUtil = new OrderCheckUtil();
    }

    @Test
    void isOrderedWithinLastThreeMonths_shouldReturnTrue_whenOrderIsWithinThreeMonths() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Order recentOrder = Mockito.mock(Order.class);
        Mockito.when(recentOrder.getOrderedAt()).thenReturn(now.minusMonths(1)); // 1개월 전 주문

        // when
        boolean result = orderCheckUtil.isOrderedWithinLastThreeMonths(recentOrder);

        // then
        assertTrue(result, "Order should be considered within the last 3 months.");
    }

    @Test
    void isOrderedWithinLastThreeMonths_shouldReturnFalse_whenOrderIsOlderThanThreeMonths() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Order oldOrder = Mockito.mock(Order.class);
        Mockito.when(oldOrder.getOrderedAt()).thenReturn(now.minusMonths(4)); // 4개월 전 주문

        // when
        boolean result = orderCheckUtil.isOrderedWithinLastThreeMonths(oldOrder);

        // then
        assertFalse(result, "Order should be considered older than 3 months.");
    }

    @Test
    void isOrderedWithinLastThreeMonths_shouldReturnTrue_whenOrderIsExactlyThreeMonthsAgo() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Order orderExactlyThreeMonthsAgo = Mockito.mock(Order.class);
        Mockito.when(orderExactlyThreeMonthsAgo.getOrderedAt()).thenReturn(now.minusMonths(3)); // 3개월 전 주문

        // when
        boolean result = orderCheckUtil.isOrderedWithinLastThreeMonths(orderExactlyThreeMonthsAgo);

        // then
        assertTrue(result, "Order should be considered within the last 3 months.");
    }
}
