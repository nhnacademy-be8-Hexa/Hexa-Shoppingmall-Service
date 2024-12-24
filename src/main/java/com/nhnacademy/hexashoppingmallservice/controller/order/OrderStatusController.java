package com.nhnacademy.hexashoppingmallservice.controller.order;


import com.nhnacademy.hexashoppingmallservice.dto.order.OrderStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderStatusService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orderStatus")
public class OrderStatusController {
    private final OrderStatusService orderStatusService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public List<OrderStatus> getAllOrderStatus() {
        return orderStatusService.getAllOrderStatus();
    }

    @PostMapping
    public ResponseEntity<OrderStatus> createOrderStatus(
            @Valid @RequestBody OrderStatusRequestDTO orderStatusRequestDTO,
            HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.status(201).body(orderStatusService.createOrderStatus(orderStatusRequestDTO));
    }

    @GetMapping("/{orderStatusId}")
    public OrderStatus getOrderStatus(@PathVariable Long orderStatusId) {
        return orderStatusService.getOrderStatus(orderStatusId);
    }

    @PutMapping("/{orderStatusId}")
    public ResponseEntity<OrderStatus> updateOrderStatus(@PathVariable Long orderStatusId,
                                                         @RequestBody OrderStatusRequestDTO orderStatusRequestDTO,
                                                         HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.ok(orderStatusService.updateOrderStatus(orderStatusId, orderStatusRequestDTO));
    }

    @DeleteMapping("/{orderStatusId}")
    public ResponseEntity<OrderStatus> deleteOrderStatus(@PathVariable Long orderStatusId, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        OrderStatus orderStatus = orderStatusService.getOrderStatus(orderStatusId);
        if (Objects.isNull(orderStatus)) {
            throw new OrderStatusNotFoundException(Long.toString(orderStatusId));
        }
        try {
            orderStatusService.deleteOrderStatus(orderStatusId);
        } catch (RuntimeException e) {
            throw new SqlQueryExecuteFailException(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
}
