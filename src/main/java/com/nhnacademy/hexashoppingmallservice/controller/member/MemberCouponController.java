package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.MemberCoupon;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberCouponController {
    private final MemberCouponService memberCouponService;

    /**
     * 특정 회원의 쿠폰 목록을 조회하는 엔드포인트
     *
     * @param memberId 쿠폰을 조회할 회원의 ID
     * @return 회원의 쿠폰 목록
     */
    @GetMapping("/auth/members/{memberId}/coupons")
    public ResponseEntity<List<MemberCoupon>> getMemberCoupons(@PathVariable String memberId) {
        List<MemberCoupon> memberCoupons = memberCouponService.getMemberCoupon(memberId);
        return ResponseEntity.ok(memberCoupons);
    }

    /**
     * 특정 회원에게 쿠폰을 생성하는 엔드포인트
     *
     * @param memberId 쿠폰을 생성할 회원의 ID
     * @param couponId 생성할 쿠폰의 ID
     * @return 생성된 MemberCoupon
     */
    @PostMapping("/auth/members/{memberId}/coupons/{couponId}")
    public ResponseEntity<MemberCoupon> createMemberCoupon(
            @PathVariable String memberId,
            @PathVariable Long couponId) {
        MemberCoupon memberCoupon = memberCouponService.createMemberCoupon(couponId, memberId);
        return new ResponseEntity<>(memberCoupon, HttpStatus.CREATED);
    }

    @DeleteMapping("/auth/members/{memberId}/coupons/{couponId}")
    public ResponseEntity<Void> deleteMemberCoupon(@PathVariable String memberId, @PathVariable Long couponId) {
        memberCouponService.deleteMemberCoupon(couponId, memberId);
        return ResponseEntity.noContent().build();
    }
}
