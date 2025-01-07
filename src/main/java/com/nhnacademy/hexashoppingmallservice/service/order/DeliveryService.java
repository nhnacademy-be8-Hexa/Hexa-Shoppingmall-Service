package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Delivery;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.DeliveryNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.order.DeliveryProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.DeliveryRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import java.util.Formatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Delivery createDelivery(DeliveryRequestDTO deliveryRequestDTO) {
        Order order = orderRepository.findById(deliveryRequestDTO.getOrderId()).orElseThrow(
                () -> {
                    String errorMessage =
                            new Formatter().format("Order ID: %s not found", deliveryRequestDTO.getOrderId().toString())
                                    .toString();
                    return new OrderNotFoundException(errorMessage);
                });

        Delivery delivery = Delivery.of(
                order,
                deliveryRequestDTO.getDeliveryAmount(),
                deliveryRequestDTO.getDeliveryDate(),
                deliveryRequestDTO.getDeliveryReleaseDate()
        );
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public List<DeliveryProjection> getDeliveries(Pageable pageable) {
        return deliveryRepository.findAllBy(pageable).getContent();
    }

    @Transactional
    public DeliveryProjection getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(
                () -> new DeliveryNotFoundException("Delivery ID: %s not found".formatted(orderId))
        );
    }

    @Transactional
    public List<DeliveryProjection> getDeliveriesByMemberId(String memberId, Pageable pageable) {
        Member member = memberRepository.findById(String.valueOf(memberId))
                .orElseThrow(() -> new MemberNotFoundException("Member ID: %s not found".formatted(memberId)));
        return deliveryRepository.findAllByOrder_Member(member, pageable).getContent();
    }

    @Transactional
    public void updateDelivery(Long orderId, DeliveryRequestDTO deliveryRequestDTO) {
        Delivery delivery = deliveryRepository.findById(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found for orderId: " + orderId));

        if (deliveryRequestDTO.getDeliveryDate() != null) {
            delivery.setDeliveryDate(deliveryRequestDTO.getDeliveryDate());
        }
        if (deliveryRequestDTO.getDeliveryReleaseDate() != null) {
            delivery.setDeliveryReleaseDate(deliveryRequestDTO.getDeliveryReleaseDate());
        }
        deliveryRepository.save(delivery);
    }


}
