package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberReport;
import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberReportAlreadyExist;
import com.nhnacademy.hexashoppingmallservice.exception.review.ReviewNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberReportRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberReportService {
    private final MemberReportRepository memberReportRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    // 사용자 신고
    @Transactional
    public void saveMemberReport(String memberId, Long reviewId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID %s does Not Exist!".formatted(memberId));
        }
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Review ID %d does Not Exist!".formatted(reviewId));
        }
        if (memberReportRepository.countByReviewReviewId(reviewId) > 0 && memberReportRepository.countByMemberMemberId(memberId) > 0) {
            throw new MemberReportAlreadyExist("Member report already exist!");
        }
        Member member = memberRepository.findById(memberId).get();
        Review review = reviewRepository.findById(reviewId).get();
        if (memberReportRepository.countByReviewReviewId(reviewId) >= 4) {
            review.setReviewIsBlocked(true);
        }
        MemberReport memberReport = MemberReport.of(member, review);
        memberReportRepository.save(memberReport);
    }

    // 신고의 총계를 확인(신고의 총계를 확인하고 리뷰의 차단 여부를 업데이트하는 것은 프론트에서 처리)
    @Transactional
    public Long totalReport(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Review ID %d does Not Exist!".formatted(reviewId));
        }
        return memberReportRepository.countByReviewReviewId(reviewId);
    }

    // 관리자 검토 결과 리뷰에 문제가 없으면 해당 리뷰에 대한 신고 전체 삭제(리뷰의 차단 해제를 업데이트하는 것은 프론트에서 처리)
    @Transactional
    public void allDelete(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Review ID %d does Not Exist!".formatted(reviewId));
        }
        memberReportRepository.deleteAllByReviewReviewId(reviewId);
        Review review = reviewRepository.findById(reviewId).get();
        review.setReviewIsBlocked(false);
    }
}

