package com.nhnacademy.hexashoppingmallservice.repository.querydsl;

import com.nhnacademy.hexashoppingmallservice.dto.book.OrderBookDTO;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderBookProjection;

import java.util.List;

public interface OrderBookRepositoryCustom {
    List<OrderBookDTO> findOrderBooksByOrderId(Long orderId);
    Long sumOrderBookAmountByOrderIdAndBookId(Long orderId, Long bookId);
}
