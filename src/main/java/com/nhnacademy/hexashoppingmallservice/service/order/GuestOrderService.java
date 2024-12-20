package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.GuestOrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.GuestOrder;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.exception.order.GuestOrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.order.GuestOrderProjection;
import com.nhnacademy.hexashoppingmallservice.repository.order.GuestOrderRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestOrderService {
    private final OrderRepository orderRepository;
    private final GuestOrderRepository guestOrderRepository;

    @Transactional
    public GuestOrder createGuestOrder(GuestOrderRequestDTO guestOrderRequestDTO) {
        Order order = orderRepository.findById(guestOrderRequestDTO.getOrderId()).orElseThrow(
                () -> new OrderNotFoundException("Order ID %s not found".formatted(guestOrderRequestDTO.getOrderId()))
        );
        GuestOrder guestOrder = GuestOrder.of(
                order,
                guestOrderRequestDTO.getGuestOrderPassword(),
                guestOrderRequestDTO.getGuestOrderNumber(),
                guestOrderRequestDTO.getGuestOrderEmail()
        );
        return guestOrderRepository.save(guestOrder);
    }

    @Transactional(readOnly = true)
    public List<GuestOrder> getGuestOrders(Pageable pageable) {
        return guestOrderRepository.findAllBy(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public GuestOrderProjection getGuestOrder(Long orderId) {
        if (!guestOrderRepository.existsById(orderId)) {
            throw new GuestOrderNotFoundException("GuestOrder ID %s not found".formatted(orderId));
        }

        return guestOrderRepository.findGuestOrderByOrderId(orderId);
    }

    @Transactional
    public GuestOrder updateGuestOrder(Long OrderId, GuestOrderRequestDTO guestOrderRequestDTO) {
        if (!guestOrderRepository.existsById(OrderId)) {
            throw new GuestOrderNotFoundException("GuestOrder ID %s not found".formatted(OrderId));
        }
        GuestOrder guestOrder = guestOrderRepository.findById(OrderId).orElseThrow();
        updateIfNotNull(guestOrderRequestDTO.getGuestOrderPassword(), guestOrder::setGuestOrderPassword);
        updateIfNotNull(guestOrderRequestDTO.getGuestOrderNumber(), guestOrder::setGuestOrderNumber);
        updateIfNotNull(guestOrderRequestDTO.getGuestOrderEmail(), guestOrder::setGuestOrderEmail);
        return guestOrder;
    }

    private <T> void updateIfNotNull(T value, Consumer<T> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }

}
