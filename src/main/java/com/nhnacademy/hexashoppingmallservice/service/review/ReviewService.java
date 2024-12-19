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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public List<ReviewProjection> getReviewsFromMember(Pageable pageable, String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID %s is not Found!".formatted(memberId));
        }

        return reviewRepository.findByMemberMemberIdAndReviewIsblockedFalse(memberId, pageable).getContent();
    }

    @Transactional(readOnly = true)
    public List<ReviewProjection> getReviewsFromBook(Pageable pageable, Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book ID %d is not Found!".formatted(bookId));
        }

        return reviewRepository.findByBookBookIdAndReviewIsblockedFalse(bookId, pageable).getContent();
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
        return reviewRepository.findAllByReviewIsblockedTrue(pageable).getContent();
    }

    @Transactional
    public void updateBlocked(Long reviewId, Boolean blocked) {
        Review review = reviewRepository.findById(reviewId).get();
        review.setReviewIsblocked(blocked);
    }
}
