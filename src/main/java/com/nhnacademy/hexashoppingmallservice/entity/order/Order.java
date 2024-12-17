package com.nhnacademy.hexashoppingmallservice.entity.order;


import com.google.common.primitives.UnsignedInteger;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    @Column(nullable = false)
    private Integer orderPrice;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @ManyToOne
    @JoinColumn(name = "wrapping_paper_id")
    private WrappingPaper wrappingPaper;

    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatus orderStatus;

    @Column(nullable = false)
    @Length(max = 5)
    private String zoneCode;

    @Column(nullable = false)
    @Length(max = 200)
    private String address;

    @Column
    @Length(max = 100)
    private String addressDetail;
}
