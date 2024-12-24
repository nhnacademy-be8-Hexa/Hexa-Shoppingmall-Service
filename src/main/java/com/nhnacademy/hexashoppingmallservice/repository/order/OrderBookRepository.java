package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.OrderBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    @Query("SELECT SUM(ob.orderBookAmount) FROM OrderBook ob WHERE ob.order.orderId = :orderId AND ob.book.bookId = :bookId")
    Long sumOrderBookAmountByOrderIdAndBookId(@Param("orderId") Long orderId, @Param("bookId") Long bookId);
}
