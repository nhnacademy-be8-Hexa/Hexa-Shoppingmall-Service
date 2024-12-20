package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.OrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.order.OrderProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.WrappingPaperRepository;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final WrappingPaperRepository wrappingPaperRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Transactional
    public Order createOrder(OrderRequestDTO orderRequestDTO) {
        String memberId = orderRequestDTO.getMemberId();
        Member member = memberRepository.findById(memberId).orElse(null);

        Long wrappingPaperId = orderRequestDTO.getWrappingPaperId();
        WrappingPaper wrappingPaper = wrappingPaperRepository.findById(wrappingPaperId).orElse(null);

        Long orderStatusId = orderRequestDTO.getOrderStatusId();
        if (!orderStatusRepository.existsById(orderStatusId)) {
            throw new OrderStatusNotFoundException(
                    "OrderStatus ID %s is not found".formatted(orderStatusId));
        }

        OrderStatus orderStatus = orderStatusRepository.findById(orderStatusId).orElseThrow();

        Order order = Order.of(
                member,
                orderRequestDTO.getOrderPrice(),
                wrappingPaper,
                orderStatus,
                orderRequestDTO.getZoneCode(),
                orderRequestDTO.getAddress(),
                orderRequestDTO.getAddressDetail()
        );

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllBy(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public List<OrderProjection> getOrdersByMemberId(String memberId, Pageable pageable) {
        return orderRepository.findOrdersByMember_MemberId(memberId, pageable).getContent();
    }


    @Transactional
    public Order updateOrder(Long orderId, OrderRequestDTO orderRequestDTO) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("%s", orderId))
        );

        updateIfNotNull(orderRequestDTO.getOrderPrice(), order::setOrderPrice);
        updateIfNotNull(orderRequestDTO.getZoneCode(), order::setZoneCode);
        updateIfNotNull(orderRequestDTO.getAddressDetail(), order::setAddressDetail);

        order.setAddress(orderRequestDTO.getAddress());

        OrderStatus orderStatus = orderStatusRepository.findById(orderRequestDTO.getOrderStatusId()).orElse(null);
        if (Objects.isNull(orderStatus)) {
            throw new OrderStatusNotFoundException(
                    "OrderStatus ID %s is not found".formatted(orderRequestDTO.getOrderStatusId()));
        }
        order.setOrderStatus(orderStatus);

        WrappingPaper wrappingpaper =
                wrappingPaperRepository.findById(orderRequestDTO.getWrappingPaperId()).orElse(null);
        order.setWrappingPaper(wrappingpaper);

        return order;
    }

    private <T> void updateIfNotNull(T value, Consumer<T> updater) {
        if (value != null) {
            updater.accept(value);
        }
    }
}
