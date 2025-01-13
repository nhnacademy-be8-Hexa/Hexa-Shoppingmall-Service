package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberCoupon;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    List<MemberCouponProjection> findByMemberMemberId(String memberId);

    // 이미 발급받은 쿠폰인지 확인
    boolean existsByCouponIdAndMember(Long couponId, Member member);

    // 유저에게 나눠준 쿠폰인지 확인
    boolean existsByCouponId(Long couponId);
}
