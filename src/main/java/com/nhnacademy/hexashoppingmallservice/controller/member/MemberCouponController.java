package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberCoupon;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberCouponService;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
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

    private final MemberService memberService;

    private final JwtUtils jwtUtils;

    /**
     * 특정 회원의 쿠폰 목록을 조회하는 엔드포인트
     *
     * @param memberId 쿠폰을 조회할 회원의 ID
     * @return 회원의 쿠폰 목록
     */
    @GetMapping("/members/{memberId}/coupons")
    public ResponseEntity<List<MemberCouponProjection>> getMemberCoupons(@PathVariable String memberId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        List<MemberCouponProjection> memberCoupons = memberCouponService.getMemberCoupon(memberId);
        return ResponseEntity.ok(memberCoupons);
    }

    /**
     * 특정 회원에게 쿠폰을 생성하는 엔드포인트
     *
     * @param memberId 쿠폰을 생성할 회원의 ID
     * @param couponId 생성할 쿠폰의 ID
     * @return 생성된 MemberCoupon
     */
    @PostMapping("/members/{memberId}/coupons/{couponId}")
    public ResponseEntity<Void> createMemberCoupon(
            @PathVariable String memberId,
            @PathVariable Long couponId, HttpServletRequest request) {
        //jwtUtils.ensureAdmin(request);
        memberCouponService.createMemberCoupon(couponId, memberId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/members/{memberId}/coupons/{couponId}")
    public ResponseEntity<Void> deleteMemberCoupon(@PathVariable String memberId, @PathVariable Long couponId, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        memberCouponService.deleteMemberCoupon(couponId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members/All/coupons")
    public ResponseEntity<List<Long>> getAllCouponId(){
        List<Long> couponIds = memberCouponService.getAllCouponId();
        return ResponseEntity.ok(couponIds);
    }

}
