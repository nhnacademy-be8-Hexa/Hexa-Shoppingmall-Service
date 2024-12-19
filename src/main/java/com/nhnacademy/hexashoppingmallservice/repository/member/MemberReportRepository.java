package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
    Long countByReviewReviewId(Long reviewId);
    Long countByMemberMemberId(String memberId);
    void deleteAllByReviewReviewId(Long reviewId);
}
