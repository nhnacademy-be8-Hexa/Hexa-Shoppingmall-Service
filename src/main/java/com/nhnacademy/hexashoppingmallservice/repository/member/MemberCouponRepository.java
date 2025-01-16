package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberCoupon;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    List<MemberCouponProjection> findByMemberMemberId(String memberId);

    @Query("SELECT m.couponId FROM MemberCoupon m")
    List<Long> findAllCouponIds();

}
