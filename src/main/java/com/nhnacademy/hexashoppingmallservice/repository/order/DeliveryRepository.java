package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Delivery;
import com.nhnacademy.hexashoppingmallservice.projection.order.DeliveryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Page<DeliveryProjection> findAllByOrder_Member(Member member, Pageable pageable);
    Page<DeliveryProjection> findAllBy(Pageable pageable);
    Optional<DeliveryProjection> findByOrderId(Long orderId);
}
