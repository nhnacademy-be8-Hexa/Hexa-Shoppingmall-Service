package com.nhnacademy.hexashoppingmallservice.service.review;

import com.nhnacademy.hexashoppingmallservice.dto.book.ReviewRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.review.ReviewNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.review.ReviewProjection;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.review.ReviewRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createReview(ReviewRequestDTO reviewRequestDTO, String memberId, Long bookId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID %s is not Found!".formatted(memberId));
        }

        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d is not Found!".formatted(bookId));
        }

        Member member = memberRepository.findById(memberId).get();

        Book book = bookRepository.findById(bookId).get();

        Review review = Review.of(
                reviewRequestDTO.getReviewContent(),
                reviewRequestDTO.getReviewRating(),
                member,
                book
        );
        reviewRepository.save(review);
    }

    // 멤버에 대한 리뷰 목록 조회
    @Transactional(readOnly = true)
    public List<ReviewProjection> getReviewsFromMember(Pageable pageable, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID %s is not Found!".formatted(memberId));
        }

        return reviewRepository.findByMemberMemberIdAndReviewIsBlockedFalse(memberId, pageable).getContent();
    }

    // 멤버에 대한 리뷰 목록 페이징을 위한 총계
    @Transactional(readOnly = true)
    public Long getReviewsFromMemberTotal(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID %s is not Found!".formatted(memberId));
        }
        return reviewRepository.countByMemberMemberIdAndReviewIsBlockedFalse(memberId);
    }

    // 특정 도서에 대한 리뷰 목록
    @Transactional(readOnly = true)
    public List<ReviewProjection> getReviewsFromBook(Pageable pageable, Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d is not Found!".formatted(bookId));
        }

        return reviewRepository.findByBookBookIdAndReviewIsBlockedFalse(bookId, pageable).getContent();
    }

    // 특정 도서에 대한 리뷰 페이징을 위한 총계
    @Transactional(readOnly = true)
    public Long getReviewsFromBookTotal(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d is not Found!".formatted(bookId));
        }
        return reviewRepository.countByBookBookIdAndReviewIsBlockedFalse(bookId);
    }

    @Transactional
    public void updateReview(ReviewRequestDTO reviewRequestDTO, Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Review ID %d is not Found!".formatted(reviewId));
        }
        Review review = reviewRepository.findById(reviewId).get();

        review.setReviewContent(reviewRequestDTO.getReviewContent());
        review.setReviewRating(reviewRequestDTO.getReviewRating());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
    @Transactional(readOnly = true)
    public List<ReviewProjection> getReviewsIsBlocked(Pageable pageable) {
        return reviewRepository.findAllByReviewIsBlockedTrue(pageable).getContent();
    }

    @Transactional
    public void updateBlocked(Long reviewId, Boolean blocked) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewNotFoundException("Review ID %d is not Found!".formatted(reviewId))
        );
        review.setReviewIsBlocked(blocked);
    }

    @Transactional(readOnly = true)
    public boolean checkReviews(String memberId, Long bookId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID %s is not Found!".formatted(memberId));
        }
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d is not Found!".formatted(bookId));
        }

        return reviewRepository.existsByMemberMemberIdAndBookBookId(memberId, bookId);
    }


    // 신고 5회 이상인 리뷰들 반환
    @Transactional(readOnly = true)
    public List<ReviewProjection> getHighlyReportedReviews(Pageable pageable) {
        long minimumReportCount = 5;
        return reviewRepository.findReviewsWithMinReports(minimumReportCount, pageable).getContent();
    }

    /**
     * 신고가 5회 이상인 리뷰의 총 개수를 조회합니다.
     *
     * @return 신고가 5회 이상인 리뷰의 총 개수
     */
    public long getTotalHighlyReportedReviews() {
        long minimumReportCount = 5;
        return reviewRepository.countReviewsWithMinReports(minimumReportCount);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageReviewRatingByBookId(Long bookId) {
        return reviewRepository.findAverageReviewRatingByBookId(bookId);
    }
}
