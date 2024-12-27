package com.nhnacademy.hexashoppingmallservice.repository.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    boolean existsByOrderStatus(@NotBlank @Length(max = 20) String orderStatus);
}
