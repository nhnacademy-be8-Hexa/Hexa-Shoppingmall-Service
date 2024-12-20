package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final Integer SIZE = 10;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.status(201).body(orderService.createOrder(orderRequestDTO));
    }

    @GetMapping
    public List<Order> getAllOrders(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, SIZE);
        return orderService.getAllOrders(pageable);
    }

    @GetMapping("/{memberId}")
    public List<Order> getOrdersByMemberId(@Valid @RequestParam(defaultValue = "0") int page,
                                           @PathVariable String memberId) {
        Pageable pageable = PageRequest.of(page, SIZE);
        return orderService.getOrdersByMemberId(memberId, pageable);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId,
                                             @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderRequestDTO));
    }

}
