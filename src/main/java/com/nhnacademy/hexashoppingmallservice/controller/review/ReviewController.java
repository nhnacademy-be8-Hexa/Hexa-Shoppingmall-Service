package com.nhnacademy.hexashoppingmallservice.controller.review;

import com.nhnacademy.hexashoppingmallservice.dto.book.ReviewRequestDTO;
import com.nhnacademy.hexashoppingmallservice.projection.review.ReviewProjection;
import com.nhnacademy.hexashoppingmallservice.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.pqc.jcajce.provider.lms.LMSKeyFactorySpi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 새로운 리뷰를 생성하는 엔드포인트
     *
     * @param reviewRequestDTO 리뷰 생성에 필요한 데이터
     * @param memberId         리뷰를 작성할 회원의 ID
     * @param bookId           리뷰를 작성할 책의 ID
     * @return 생성된 리뷰의 상태 코드
     */
    @PostMapping("/auth/members/{memberId}/books/{bookId}/reviews")
    public ResponseEntity<Void> createReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @PathVariable String memberId,
            @PathVariable Long bookId) {
        reviewService.createReview(reviewRequestDTO, memberId, bookId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 특정 회원의 리뷰 목록을 조회하는 엔드포인트
     *
     * @param memberId 리뷰를 조회할 회원의 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 회원의 리뷰 목록 페이지
     */
    @GetMapping("/auth/members/{memberId}/reviews")
    public ResponseEntity<List<ReviewProjection>> getReviewsFromMember(
            @PathVariable String memberId,
            Pageable pageable) {
        List<ReviewProjection> reviews = reviewService.getReviewsFromMember(pageable, memberId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 리뷰를 수정하는 엔드포인트
     *
     * @param reviewRequestDTO 수정할 리뷰의 데이터
     * @param reviewId         수정할 리뷰의 ID
     * @return 수정된 리뷰의 상태 코드
     */
    @PutMapping("/auth/reviews/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @PathVariable Long reviewId) {
        reviewService.updateReview(reviewRequestDTO, reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/auth/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewProjection>> getReviewsFromBook(@PathVariable Long bookId, Pageable pageable) {
        List<ReviewProjection> reviews = reviewService.getReviewsFromBook(pageable, bookId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 리뷰를 삭제하는 엔드포인트
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @return 삭제된 리뷰의 상태 코드
     */
    @DeleteMapping("/auth/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/admin/reviews")
    public ResponseEntity<List<ReviewProjection>> getReviewsFromAdmin(Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsIsBlocked(pageable));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> updateReviewBlock(@PathVariable Long reviewId, @RequestParam boolean block) {
        reviewService.updateBlocked(reviewId, block);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
