package com.nhnacademy.hexashoppingmallservice.repository.review;

import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import com.nhnacademy.hexashoppingmallservice.projection.review.ReviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<ReviewProjection> findByMemberMemberIdAndReviewIsblockedFalse(String memberId, Pageable pageable);
    Page<ReviewProjection> findByBookBookIdAndReviewIsblockedFalse(Long bookId, Pageable pageable);
    Page<ReviewProjection> findAllByReviewIsblockedTrue(Pageable pageable);
}
