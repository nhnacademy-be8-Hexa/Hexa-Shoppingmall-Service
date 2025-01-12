package com.nhnacademy.hexashoppingmallservice.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
@Table(name = "member_order_summary_3m")
public class MemberOrderSummary3M {

    @Id
    @Column(nullable = false, unique = true)
    private String memberId; // 멤버 ID가 primary key

    @Column(nullable = false)
    private Integer totalOrderPrice; // 합산된 주문 가격

    // 생성자 메서드: 주문 가격 합산 후 요약을 생성
    public static MemberOrderSummary3M of(String memberId, Integer totalOrderPrice) {
        return MemberOrderSummary3M.builder()
                .memberId(memberId)
                .totalOrderPrice(totalOrderPrice)
                .build();
    }
}
