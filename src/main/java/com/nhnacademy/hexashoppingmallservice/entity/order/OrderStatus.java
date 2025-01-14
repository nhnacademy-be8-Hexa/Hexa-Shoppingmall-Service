package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@NoArgsConstructor
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderStatusId;

    @Setter
    @NotBlank
    @Length(max = 20)
    private String orderStatus;
    // 배송중
    // 대기
    // 완료
    // 반품 대기
    // 반품 완료
    // 주문 취소

    @Builder
    private OrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public static OrderStatus of(String orderStatus) {
        return builder()
                .orderStatus(orderStatus)
                .build();
    }

}
