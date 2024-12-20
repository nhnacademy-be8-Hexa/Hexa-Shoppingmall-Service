package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Delivery {
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer deliveryAmount;

    @Setter
    private LocalDateTime deliveryDate;

    @Setter
    private LocalDateTime deliveryReleaseDate;

    public static Delivery of(Order order, Integer deliveryAmount) {
        return Delivery.builder()
                .order(order)
                .deliveryAmount(deliveryAmount)
                .build();
    }
}
