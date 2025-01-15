package com.nhnacademy.hexashoppingmallservice.repository.querydsl.impl;

import com.nhnacademy.hexashoppingmallservice.dto.book.OrderBookDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderBook;
import com.nhnacademy.hexashoppingmallservice.entity.order.QOrderBook;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.OrderBookRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderBookRepositoryCustomImpl extends QuerydslRepositorySupport implements OrderBookRepositoryCustom {

    public OrderBookRepositoryCustomImpl() {
        super(OrderBook.class);
    }

    @Override
    public List<OrderBookDTO> findOrderBooksByOrderId(Long orderId) {
        QOrderBook orderBook = QOrderBook.orderBook;

        return from(orderBook)
                .select(Projections.constructor(OrderBookDTO.class,
                        orderBook.order.orderId,
                        orderBook.book.bookId,
                        orderBook.book.bookTitle,
                        orderBook.orderBookAmount,
                        orderBook.book.bookPrice,
                        orderBook.couponId
                ))
                .where(orderBook.order.orderId.eq(orderId))
                .fetch();
    }

    @Override
    public Long sumOrderBookAmountByOrderIdAndBookId(Long orderId, Long bookId) {
        QOrderBook orderBook = QOrderBook.orderBook;

        // Querydsl 쿼리 작성
        JPQLQuery<Integer> query = from(orderBook)
                .select(orderBook.orderBookAmount.sum())
                .where(
                        orderBook.order.orderId.eq(orderId)
                                .and(orderBook.book.bookId.eq(bookId))
                );

        return query.fetchOne().longValue();
    }
}
