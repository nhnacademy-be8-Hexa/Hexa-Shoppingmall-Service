package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.projection.order.OrderBookProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderBookController {

    private final OrderBookService orderBookService;

    @Autowired
    public OrderBookController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    /**
     * 특정 주문 ID에 대한 주문 상세 정보를 반환합니다.
     * @param orderId 주문 ID
     * @return 주문 상세 정보 리스트
     */

    @GetMapping("api/orders/{orderId}/orderBooks")
    public ResponseEntity<List<OrderBookProjection>> getOrderBooks(@PathVariable Long orderId) {
        List<OrderBookProjection> orderBooks = orderBookService.getOrderBooksByOrderId(orderId);
        return ResponseEntity.ok(orderBooks);
    }
}
