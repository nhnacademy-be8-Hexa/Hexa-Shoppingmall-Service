package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;

    public OrderStatus getOrderStatus(Long orderId) {
        Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findById(orderId);
        if(orderStatusOptional.isEmpty()) {
            throw new OrderStatusNotFoundException("order status %d not found.".formatted(orderId));
        }
        return orderStatusOptional.get();
    }

    public List<OrderStatus> getAll() {
        return orderStatusRepository.findAll();
    }

    @Transactional
    public OrderStatus create(OrderStatus orderStatus) {
        return orderStatusRepository.save(orderStatus);
    }

    @Transactional
    public OrderStatus update(Long orderStatusId, OrderStatusRequestDTO requestDTO) {
        OrderStatus orderStatus = orderStatusRepository.findById(orderStatusId).orElse(null);
        if(orderStatus == null) {
            throw new OrderStatusNotFoundException("order status %d not found.".formatted(orderStatusId));
        }
        orderStatus.setOrderStatus(orderStatus.getOrderStatus());
        return orderStatus;
    }

    @Transactional
    public void delete(Long orderStatusId) {
        orderStatusRepository.deleteById(orderStatusId);
    }

}
