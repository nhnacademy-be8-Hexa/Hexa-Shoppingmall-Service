package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Delivery;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.DeliveryNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.DeliveryRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Formatter;
import java.util.List;

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
            String errorMessage = new Formatter().format("Order ID: %s not found", deliveryRequestDTO.getOrderId().toString()).toString();
            return new OrderNotFoundException(errorMessage);
        });

        Delivery delivery = Delivery.of(
                order,
                deliveryRequestDTO.getDeliveryAmount()

        );
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public List<Delivery> getDeliveries() { return deliveryRepository.findAll();}

    @Transactional
    public Delivery getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findById(orderId).orElseThrow(
                () -> new DeliveryNotFoundException("Delivery ID: %s not found". formatted(orderId))
        );
    }

    @Transactional
    public Delivery getDeliveryByMemberId(String memberId) {
        Member member = memberRepository.findById(String.valueOf(memberId))
                .orElseThrow(() -> new MemberNotFoundException("Member ID: %s not found".formatted(memberId)));
        return deliveryRepository.findByOrder_Member(member).orElseThrow(
                () -> new DeliveryNotFoundException("Delivery ID: %s not found". formatted(memberId))
        );
    }

    @Transactional
    public Delivery updateDelivery(Long orderId, DeliveryRequestDTO deliveryRequestDTO) {
        Delivery delivery = deliveryRepository.findById(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found for orderId: " + orderId));

        if (deliveryRequestDTO.getDeliveryDate() != null) {
            delivery.setDeliveryDate(deliveryRequestDTO.getDeliveryDate());
        }
        if (deliveryRequestDTO.getDeliveryReleaseDate() != null) {
            delivery.setDeliveryReleaseDate(deliveryRequestDTO.getDeliveryReleaseDate());
        }
        return deliveryRepository.save(delivery);
    }



}
