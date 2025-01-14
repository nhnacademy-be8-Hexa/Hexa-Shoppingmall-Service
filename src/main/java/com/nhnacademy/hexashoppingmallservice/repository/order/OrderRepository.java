package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderProjection;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<OrderProjection> findAllBy(Pageable pageable);
    Page<OrderProjection> findByMember_MemberId(String memberId, Pageable pageable);
    Optional<OrderProjection> findByOrderId(Long orderId);
    Boolean existsByOrderIdAndMember_MemberId(Long orderId, @Length(max = 50) String memberMemberId);
    Long countAllByMember_MemberId(@Length(max = 50) String memberMemberId);
    // 주문 상태 목록 페이징 처리
    Page<OrderProjection> findByOrderStatus_OrderStatusId(Long orderStatusId, Pageable pageable);
    // 주문 상태별 총 개수
    Long countByOrderStatus_OrderStatusId(Long orderStatusId);
}
