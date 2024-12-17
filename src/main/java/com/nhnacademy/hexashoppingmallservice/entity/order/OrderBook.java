package com.nhnacademy.hexashoppingmallservice.entity.order;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class OrderBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long orderBookId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer orderBookAmount;

    @Column(columnDefinition = "bigint")
    private Long coupon_id;
}
