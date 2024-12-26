package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.MemberCoupon;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    List<MemberCouponProjection> findByMemberMemberId(String memberId);
}
