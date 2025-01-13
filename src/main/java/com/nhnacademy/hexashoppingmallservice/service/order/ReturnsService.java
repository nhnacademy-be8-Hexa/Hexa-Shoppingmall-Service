package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.Returns;
import com.nhnacademy.hexashoppingmallservice.entity.order.ReturnsReason;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.DeliveryNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.ReturnsNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.ReturnsReasonNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.OrderRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.ReturnsReasonRepository;
import com.nhnacademy.hexashoppingmallservice.repository.order.ReturnsRepository;
import com.nhnacademy.hexashoppingmallservice.util.OrderCheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Formatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnsService {
    private final ReturnsRepository returnsRepository;
    private final OrderRepository orderRepository;
    private final ReturnsReasonRepository returnsReasonRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Returns createReturns(ReturnsRequestDTO returnsRequestDTO) {
        Order order = orderRepository.findById(returnsRequestDTO.getOrderId()).orElseThrow(
                () -> {
                    String errorMessage = new Formatter().format("Order with id '%s' not found", returnsRequestDTO.getOrderId()).toString();
                    return new OrderNotFoundException(errorMessage);
                }
        );

        ReturnsReason returnsReason = returnsReasonRepository.findById(returnsRequestDTO.getReturnsReasonId()).orElseThrow(
                () -> {
                    String errorMessage = new Formatter().format("ReturnsReason with id '%s' not found", returnsRequestDTO.getReturnsReasonId()).toString();
                    return new ReturnsReasonNotFoundException(errorMessage);
                }
        );

        Returns returns = Returns.of(
                order,
                returnsReason,
                returnsRequestDTO.getReturnsDetail()

        );
        return returnsRepository.save(returns);
    }

    @Transactional
    public List<Returns> getReturns(Pageable pageable) {
        return returnsRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Returns getReturnsByOrderId(Long orderId) {
        return returnsRepository.findById(orderId).orElseThrow(
                () -> new ReturnsNotFoundException("Returns with id '%s' not found" .formatted(orderId))
        );
    }

    @Transactional
    public Returns getReturnsByReturnsReasonId(Long returnsReasonId) {
        return returnsRepository.findById(returnsReasonId).orElseThrow(
                () -> new ReturnsNotFoundException("Returns with id '%s' not found" .formatted(returnsReasonId))
        );
    }

    @Transactional
    public Returns getReturnsByMemberId(String memberId) {
       Member member = memberRepository.findById(memberId).orElseThrow(
               () -> new MemberNotFoundException("Member with id '%s' not found" .formatted(memberId)));
               return returnsRepository.findByOrder_Member(member).orElseThrow(
                ()-> new OrderNotFoundException("Order with id '%s' not found" .formatted(memberId))

       );
    }

    @Transactional
    public Returns updateReturns(Long orderId, ReturnsRequestDTO returnsRequestDTO) {
        Returns returns = returnsRepository.findById(orderId)
                .orElseThrow(() -> new ReturnsNotFoundException("Returns not found for orderId: " + orderId));
        returns.setReturnsDetail(returnsRequestDTO.getReturnsDetail());

        return returnsRepository.save(returns);
    }

    @Transactional
    public void deleteReturns(Long orderId) {
        Returns returns = returnsRepository.findById(orderId)
                .orElseThrow(() -> new ReturnsNotFoundException("Returns not found for orderId: " + orderId));
        returnsRepository.deleteById(orderId);
    }


}
