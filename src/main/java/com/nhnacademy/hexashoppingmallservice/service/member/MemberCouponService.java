package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberCoupon;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberCouponRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberCouponService {
    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<MemberCouponProjection> getMemberCoupon(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        return memberCouponRepository.findByMemberMemberId(memberId);
    }

    @Transactional
    public void createMemberCoupon(Long couponId, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        Member member = memberRepository.findById(memberId).get();
        MemberCoupon memberCoupon = MemberCoupon.of(couponId, member);
        memberCouponRepository.save(memberCoupon);
    }

    @Transactional
    public void deleteMemberCoupon(Long couponId, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        if (!memberCouponRepository.existsById(couponId)) {
            throw new MemberNotFoundException("Coupon %d is not found".formatted(couponId));
        }
        MemberCoupon memberCoupon = memberCouponRepository.findById(couponId).get();
        memberCouponRepository.delete(memberCoupon);
    }
}
