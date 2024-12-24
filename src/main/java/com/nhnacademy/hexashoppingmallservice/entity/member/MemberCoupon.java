package com.nhnacademy.hexashoppingmallservice.entity.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Setter
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberCouponId;

    @Column(nullable = false)
    @Setter
    @NotNull
    private Long couponId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static MemberCoupon of(Long couponId,Member member) {
        return builder()
                .couponId(couponId)
                .member(member)
                .build();
    }

}
