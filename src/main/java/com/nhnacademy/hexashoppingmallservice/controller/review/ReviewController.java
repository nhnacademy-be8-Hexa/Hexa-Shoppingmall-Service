package com.nhnacademy.hexashoppingmallservice.controller.review;

import com.nhnacademy.hexashoppingmallservice.dto.book.ReviewRequestDTO;
import com.nhnacademy.hexashoppingmallservice.projection.review.ReviewProjection;
import com.nhnacademy.hexashoppingmallservice.service.review.ReviewService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.pqc.jcajce.provider.lms.LMSKeyFactorySpi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtUtils jwtUtils;

    /**
     * 새로운 리뷰를 생성하는 엔드포인트
     *
     * @param reviewRequestDTO 리뷰 생성에 필요한 데이터
     * @param memberId         리뷰를 작성할 회원의 ID
     * @param bookId           리뷰를 작성할 책의 ID
     * @return 생성된 리뷰의 상태 코드
     */
    @PostMapping("/members/{memberId}/books/{bookId}/reviews")
    public ResponseEntity<Void> createReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @PathVariable String memberId,
            @PathVariable Long bookId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
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
    @GetMapping("/members/{memberId}/reviews")
    public ResponseEntity<List<ReviewProjection>> getReviewsFromMember(
            @PathVariable String memberId,
            Pageable pageable, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        List<ReviewProjection> reviews = reviewService.getReviewsFromMember(pageable, memberId);
        return ResponseEntity.ok(reviews);
    }

    // 특정 회원의 리뷰 총계를 조회
    @GetMapping("/members/{memberId}/reviews/total")
    public ResponseEntity<Long> getTotalReviews(@PathVariable String memberId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        return ResponseEntity.ok(reviewService.getReviewsFromMemberTotal(memberId));
    }

    /**
     * 특정 리뷰를 수정하는 엔드포인트
     *
     * @param reviewRequestDTO 수정할 리뷰의 데이터
     * @param reviewId         수정할 리뷰의 ID
     * @return 수정된 리뷰의 상태 코드
     */
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
            @PathVariable Long reviewId, HttpServletRequest request) {
        String token = jwtUtils.getTokenFromRequest(request);
        String memberId = jwtUtils.getUsernameFromToken(token);
        jwtUtils.ensureUserAccess(request, memberId);
        reviewService.updateReview(reviewRequestDTO, reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewProjection>> getReviewsFromBook(@PathVariable Long bookId, Pageable pageable) {
        List<ReviewProjection> reviews = reviewService.getReviewsFromBook(pageable, bookId);
        return ResponseEntity.ok(reviews);
    }

    // 특정 도서의 리뷰 총계
    @GetMapping("/books/{bookId}/reviews/total")
    public ResponseEntity<Long> getTotalReviewsFromBook(@PathVariable Long bookId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsFromBookTotal(bookId));
    }

    /**
     * 특정 리뷰를 삭제하는 엔드포인트
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @return 삭제된 리뷰의 상태 코드
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, HttpServletRequest request) {
        String token = jwtUtils.getTokenFromRequest(request);
        String memberId = jwtUtils.getUsernameFromToken(token);
        jwtUtils.ensureUserAccess(request, memberId);
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewProjection>> getReviewsFromAdmin(Pageable pageable, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.ok(reviewService.getReviewsIsBlocked(pageable));
    }

    @PutMapping("/reviews/{reviewId}/block")
    public ResponseEntity<Void> updateReviewBlock(@PathVariable Long reviewId, @RequestParam boolean block, HttpServletRequest request) {
        String token = jwtUtils.getTokenFromRequest(request);
        String memberId = jwtUtils.getUsernameFromToken(token);
        jwtUtils.ensureUserAccess(request, memberId);
        reviewService.updateBlocked(reviewId, block);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 신고 5회 이상의 리뷰 목록 조회
     *
     *
     */
    @GetMapping("/reviews/highReport")
    public ResponseEntity<List<ReviewProjection>> getReviewsFromHighReport(Pageable pageable, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.ok(reviewService.getHighlyReportedReviews(pageable));
    }

    // 신고 5회 이상의 리뷰 목록 조회 페이징을 위한 총계
    @GetMapping("/reviews/highReport/total")
    public ResponseEntity<Long> getReviewsFromHighReportTotal() {
        return ResponseEntity.ok(reviewService.getTotalHighlyReportedReviews());
    }

}
