package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    List<MemberCoupon> findByMemberMemberId(String memberId);
}
