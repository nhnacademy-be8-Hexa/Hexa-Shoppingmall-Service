package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderStatusRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    @Transactional(readOnly = true)
    public OrderStatus getOrderStatus(Long orderStatusId) {
        Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findById(orderStatusId);
        if (orderStatusOptional.isEmpty()) {
            throw new OrderStatusNotFoundException("order status %d not found.".formatted(orderStatusId));
        }
        return orderStatusOptional.get();
    }

    @Transactional(readOnly = true)
    public List<OrderStatus> getAllOrderStatus() {
        return orderStatusRepository.findAll();
    }

    @Transactional
    public OrderStatus createOrderStatus(OrderStatusRequestDTO orderStatusRequestDTO) {
        OrderStatus orderStatus = OrderStatus.of(
                orderStatusRequestDTO.getOrderStatus()
        );
        return orderStatusRepository.save(orderStatus);
    }


    @Transactional
    public OrderStatus updateOrderStatus(Long orderStatusId, OrderStatusRequestDTO orderStatusRequestDTO) {
        OrderStatus orderStatus = orderStatusRepository.findById(orderStatusId).orElse(null);
        if (orderStatus == null) {
            throw new OrderStatusNotFoundException("order status %d not found.".formatted(orderStatusId));
        }
        orderStatus.setOrderStatus(orderStatusRequestDTO.getOrderStatus());
        return orderStatus;
    }


    @Transactional
    public void deleteOrderStatus(Long orderStatusId) {
        orderStatusRepository.deleteById(orderStatusId);
    }

}
