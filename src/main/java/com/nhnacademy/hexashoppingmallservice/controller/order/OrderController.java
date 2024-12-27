package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class OrderController {
    private final Integer SIZE = 10;
    private final OrderService orderService;
    private final JwtUtils jwtUtils;

    @PostMapping("/api/orders")
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO,
                                             @RequestParam List<Long> bookIds,
                                             @RequestParam List<Integer> amounts,
                                             @RequestParam(required = false) Long couponId) {
        orderService.createOrder(orderRequestDTO, bookIds, amounts, couponId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/orders")
    public ResponseEntity<List<OrderProjection>> getAllOrders(@RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        Pageable pageable = PageRequest.of(page, SIZE);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/api/members/{memberId}/orders")
    public ResponseEntity<List<OrderProjection>> getOrdersByMemberId(@Valid @RequestParam(defaultValue = "0") int page,
                                           @PathVariable String memberId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        Pageable pageable = PageRequest.of(page, SIZE);
        return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId, pageable));
    }

    @GetMapping("/api/orders/{orderId}")
    public ResponseEntity<OrderProjection> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PutMapping("/api/orders/{orderId}")
    public ResponseEntity<Void> updateOrder(@PathVariable Long orderId,
                                             @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        orderService.updateOrder(orderId, orderRequestDTO);
        return ResponseEntity.noContent().build();
    }

    // 특정 주문의 주문한 책 수량
    @GetMapping("api/orders/{orderId}/books/{bookId}")
    public ResponseEntity<Long> getOrderAmount(@PathVariable Long orderId, @PathVariable Long bookId) {
        return ResponseEntity.ok(orderService.getAmount(orderId, bookId));
    }
}
