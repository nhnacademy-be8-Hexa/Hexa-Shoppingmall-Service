package com.nhnacademy.hexashoppingmallservice.repository.review;

import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import com.nhnacademy.hexashoppingmallservice.projection.review.ReviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<ReviewProjection> findByMemberMemberIdAndReviewIsBlockedFalse(String memberId, Pageable pageable);
    Long countByMemberMemberIdAndReviewIsBlockedFalse(String memberId);
    Page<ReviewProjection> findByBookBookIdAndReviewIsBlockedFalse(Long bookId, Pageable pageable);
    Long countByBookBookIdAndReviewIsBlockedFalse(Long bookId);
    Page<ReviewProjection> findAllByReviewIsBlockedTrue(Pageable pageable);
    /**
     * 신고가 특정 회수 이상인 리뷰들을 조회합니다.
     * 차단 여부와 관계없이 모든 리뷰를 포함합니다.
     *
     * @param reportCount 신고 최소 회수
     * @param pageable    페이징 정보
     * @return 신고가 최소 회수 이상인 리뷰의 페이지
     */
    @Query("SELECT r FROM Review r " +
            "WHERE (SELECT COUNT(mr) FROM MemberReport mr WHERE mr.review = r) >= :reportCount")
    Page<ReviewProjection> findReviewsWithMinReports(@Param("reportCount") long reportCount, Pageable pageable);

    /**
     * 신고가 특정 회수 이상인 리뷰의 총 개수를 조회합니다.
     * 차단 여부와 관계없이 모든 리뷰를 포함합니다.
     *
     * @param reportCount 신고 최소 회수
     * @return 신고가 최소 회수 이상인 리뷰의 총 개수
     */
    @Query("SELECT COUNT(r) FROM Review r " +
            "WHERE (SELECT COUNT(mr) FROM MemberReport mr WHERE mr.review = r) >= :reportCount")
    long countReviewsWithMinReports(@Param("reportCount") long reportCount);

    boolean existsByMemberMemberIdAndBookBookId(String memberId, Long bookId);
}
