package com.nhnacademy.hexashoppingmallservice.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
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

    public static MemberCoupon of(Long couponId, Member member) {
        return builder()
                .couponId(couponId)
                .member(member)
                .build();
    }

}
