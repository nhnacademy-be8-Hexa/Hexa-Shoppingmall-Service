package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.OrderBook;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderBookProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    Page<OrderBookProjection> findByOrderOrderId(Long orderId, Pageable pageable);
    Page<OrderBookProjection> findByBookBookId(Long bookId, Pageable pageable);
    Page<OrderBookProjection> findAllProjectedBy(Pageable pageable);
    List<OrderBook> findByOrderOrderId(Long orderId);
}

