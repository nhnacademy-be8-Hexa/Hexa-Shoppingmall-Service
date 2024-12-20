package com.nhnacademy.hexashoppingmallservice.controller.order;


import com.nhnacademy.hexashoppingmallservice.dto.order.OrderStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orderStatus")
public class OrderStatusController {
    private final OrderStatusService orderStatusService;

    @GetMapping
    public List<OrderStatus> getAllOrderStatus() {
        return orderStatusService.getAllOrderStatus();
    }

    @PostMapping
    public ResponseEntity<OrderStatus> createOrderStatus(
            @Valid @RequestBody OrderStatusRequestDTO orderStatusRequestDTO) {
        return ResponseEntity.status(201).body(orderStatusService.createOrderStatus(orderStatusRequestDTO));
    }

    @GetMapping("/{orderStatusId}")
    public OrderStatus getOrderStatus(@PathVariable Long orderStatusId) {
        return orderStatusService.getOrderStatus(orderStatusId);
    }

    @PatchMapping("/{orderStatusId}")
    public ResponseEntity<OrderStatus> updateOrderStatus(@PathVariable Long orderStatusId,
                                                         @RequestBody OrderStatusRequestDTO orderStatusRequestDTO) {
        return ResponseEntity.ok(orderStatusService.updateOrderStatus(orderStatusId, orderStatusRequestDTO));
    }

    @DeleteMapping("/{orderStatusId}")
    public ResponseEntity<OrderStatus> deleteOrderStatus(@PathVariable Long orderStatusId) {
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
