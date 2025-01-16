package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderRequestDTO;
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
    public ResponseEntity<Long> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO,
                                             @RequestParam List<Long> bookIds,
                                             @RequestParam List<Integer> amounts,
                                             @RequestParam(required = false) Long couponId) {
        Long id = orderService.createOrder(orderRequestDTO, bookIds, amounts, couponId);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/api/orders")
    public ResponseEntity<List<OrderProjection>> getAllOrders(Pageable pageable, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/api/members/{memberId}/orders")
    public ResponseEntity<List<OrderProjection>> getOrdersByMemberId(
            @PathVariable String memberId,
            Pageable pageable,
            HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId, pageable));
    }

    @GetMapping("/api/orders/{orderId}")
    public ResponseEntity<OrderProjection> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PutMapping("/api/orders/{orderId}")
    public ResponseEntity<Void> updateOrder(@PathVariable Long orderId,
                                             @RequestBody OrderRequestDTO orderRequestDTO) {
        orderService.updateOrder(orderId, orderRequestDTO);
        return ResponseEntity.noContent().build();
    }

    // 특정 주문의 주문한 책 수량
    @GetMapping("api/orders/{orderId}/books/{bookId}")
    public ResponseEntity<Long> getOrderAmount(@PathVariable Long orderId, @PathVariable Long bookId) {
        return ResponseEntity.ok(orderService.getAmount(orderId, bookId));
    }

    // 특정 도서가 특정 멤버가 주문한 사항에 존재하는지를 조회
    @GetMapping("/api/members/{memberId}/orders/books/{bookId}")
    public ResponseEntity<Boolean> getCheckOrderBook(@PathVariable String memberId, @PathVariable Long bookId) {
        return ResponseEntity.ok(orderService.checkOrderBook(memberId, bookId));
    }

    // orderId 랑 memberId 맞는지 확인하는 메서드
    @GetMapping("api/orders/{orderId}/{memberId}")
    public ResponseEntity<Boolean> existsOrderIdAndMember_MemberId(@PathVariable Long orderId, @PathVariable String memberId){
        return ResponseEntity.ok(orderService.existsOrderIdAndMember_MemberId(orderId,memberId));
    }

    @GetMapping("api/orders/count/{memberId}")
    public ResponseEntity<Long> countAllByMember_MemberId(@PathVariable String memberId){
        Long result = orderService.countAllByMember_MemberId(memberId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/orders/count")
    public ResponseEntity<Long> getTotalOrderCount() {
        Long count = orderService.countAllOrders();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/api/orders/status")
    public ResponseEntity<List<OrderProjection>> getOrderStatus(@RequestParam Long statusId, Pageable pageable) {
        List<OrderProjection> orders = orderService.getOrdersByStatusId(statusId, pageable);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/api/orders/status/{statusId}/count")
    public ResponseEntity<Long> countOrdersByStatus(@PathVariable Long statusId) {
        Long count = orderService.countOrdersByStatusId(statusId);
        return ResponseEntity.ok(count);
    }
}
