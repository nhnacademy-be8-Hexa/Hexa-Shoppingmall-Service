package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.GuestOrder;
import com.nhnacademy.hexashoppingmallservice.projection.order.GuestOrderPasswordProjection;
import com.nhnacademy.hexashoppingmallservice.projection.order.GuestOrderProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestOrderRepository extends JpaRepository<GuestOrder, Long> {
    GuestOrderProjection findByOrderId(Long orderId);

    Page<GuestOrderProjection> findAllBy(Pageable pageable);

    boolean existsByOrderId(Long orderId);

    Optional<GuestOrderPasswordProjection> findGuestOrderPasswordByOrderId(Long orderId);
}
