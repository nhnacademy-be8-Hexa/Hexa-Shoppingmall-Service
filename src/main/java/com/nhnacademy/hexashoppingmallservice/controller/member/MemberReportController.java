package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.service.member.MemberReportService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberReportController {
    private final MemberReportService memberReportService;
    private final JwtUtils jwtUtils;

    /**
     * 새로운 회원 신고를 생성하는 엔드포인트
     *
     * @param memberId 신고를 작성할 회원의 ID
     * @param reviewId 신고할 리뷰의 ID
     * @return 생성된 신고의 상태 코드
     */
    @PostMapping("/reports/members/{memberId}/reviews/{reviewId}")
    public ResponseEntity<Void> saveMemberReport(
            @PathVariable String memberId,
            @PathVariable Long reviewId,
            HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        memberReportService.saveMemberReport(memberId, reviewId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 특정 리뷰의 신고 총계를 조회하는 엔드포인트
     *
     * @param reviewId 신고 총계를 조회할 리뷰의 ID
     * @return 신고 총계
     */
    @GetMapping("/admin/reports/reviews/{reviewId}/count")
    public ResponseEntity<Long> getTotalReport(
            @PathVariable Long reviewId, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        Long totalReport = memberReportService.totalReport(reviewId);
        return ResponseEntity.ok(totalReport);
    }

    /**
     * 관리자용 특정 리뷰의 모든 신고를 삭제하는 엔드포인트
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @return 삭제된 신고의 상태 코드
     */
    @DeleteMapping("/admin/reports/admin/reviews/{reviewId}")
    public ResponseEntity<Void> allDelete(
            @PathVariable Long reviewId, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        memberReportService.allDelete(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
