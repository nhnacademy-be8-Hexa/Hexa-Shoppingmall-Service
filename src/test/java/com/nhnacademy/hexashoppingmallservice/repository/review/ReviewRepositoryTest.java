package com.nhnacademy.hexashoppingmallservice.repository.review;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import com.nhnacademy.hexashoppingmallservice.projection.review.ReviewProjection;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MemberStatusRepository memberStatusRepository;

    private Member member;
    private Book book;
    private Rating rating;
    private MemberStatus memberStatus;
    private Review review1;
    private Review review2;
    private Review review3;

    /**
     * 테스트 데이터를 초기화하고 저장하는 메서드
     */
    @BeforeEach
    void setUp() {
        // Rating 생성
        rating = Rating.of(
                "Good",
                10
        );
        entityManager.persist(rating);

        // MemberStatus 생성
        memberStatus = MemberStatus.of(
                "active"
        );
        entityManager.persist(memberStatus);

        // Member 생성
        member = Member.of(
                "member123",
                "password123",
                "John Doe",
                "01012345678",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                rating,
                memberStatus
        );
        member.setMemberCreatedAt(LocalDateTime.now());
        entityManager.persist(member);

        BookStatus bookStatus = BookStatus.of(
                "sell"
        );

        entityManager.persist(bookStatus);

        Publisher publisher = Publisher.of(
                "test"
        );

        entityManager.persist(publisher);

        // Book 생성
        book = Book.of(
                "Book Title",
                "Book Description",
                LocalDate.now(),
                1234567890123L,
                20000,
                18000,
                publisher,
                bookStatus
        );
        entityManager.persist(book);

        // Review1 생성 (차단되지 않은 리뷰)
        review1 = Review.of(
                "Great book!",
                new BigDecimal("5"),
                member,
                book
        );
        entityManager.persist(review1);

        // Review2 생성 (차단되지 않은 리뷰)
        review2 = Review.of(
                "Very informative.",
                new BigDecimal("4"),
                member,
                book
        );
        entityManager.persist(review2);

        // Review3 생성 (차단된 리뷰)
        review3 = Review.of(
                "Spam review.",
                new BigDecimal("1"),
                member,
                book
        );
        review3.setReviewIsblocked(true);
        entityManager.persist(review3);
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * findByMemberMemberIdAndReviewIsblockedFalse 메서드 테스트
     */
    @Test
    @Transactional
    @DisplayName("findByMemberMemberIdAndReviewIsblockedFalse - 특정 회원의 차단되지 않은 리뷰 목록을 조회한다")
    void findByMemberMemberIdAndReviewIsblockedFalse() {
        // Arrange
        String memberId = "member123";
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        Page<ReviewProjection> reviews = reviewRepository.findByMemberMemberIdAndReviewIsblockedFalse(memberId, pageable);

        // Assert
        assertThat(reviews).isNotNull();
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        for (ReviewProjection review : reviews) {
            assertThat(review.getMember().getMemberId()).isEqualTo(memberId);
        }
    }

    /**
     * findByBookBookIdAndReviewIsblockedFalse 메서드 테스트
     */
    @Test
    @DisplayName("findByBookBookIdAndReviewIsblockedFalse - 특정 책의 차단되지 않은 리뷰 목록을 조회한다")
    void findByBookBookIdAndReviewIsblockedFalse() {
        // Arrange
        Long bookId = book.getBookId(); // 동적으로 book ID 가져오기
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        Page<ReviewProjection> reviews = reviewRepository.findByBookBookIdAndReviewIsblockedFalse(bookId, pageable);

        // Assert
        assertThat(reviews).isNotNull();
        assertThat(reviews.getTotalElements()).isEqualTo(2);
    }

    /**
     * findAllByReviewIsblockedTrue 메서드 테스트
     */
    @Test
    @DisplayName("findAllByReviewIsblockedTrue - 모든 차단된 리뷰를 조회한다")
    void findAllByReviewIsblockedTrue() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        Page<ReviewProjection> blockedReviews = reviewRepository.findAllByReviewIsblockedTrue(pageable);

        // Assert
        assertThat(blockedReviews).isNotNull();
        assertThat(blockedReviews.getTotalElements()).isEqualTo(1);
    }

    /**
     * findById 메서드 테스트
     */
    @Test
    @DisplayName("findById - 특정 리뷰를 ID로 조회한다")
    void findById() {
        // Arrange
        Long reviewId = review1.getReviewId(); // 동적으로 review ID 가져오기

        // Act
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        // Assert
        assertThat(reviewOpt).isPresent();
        Review review = reviewOpt.get();
        assertThat(review.getReviewId()).isEqualTo(reviewId);
        assertThat(review.getReviewContent()).isEqualTo("Great book!");
    }

    /**
     * save 메서드 테스트
     */
    @Test
    @DisplayName("save - 리뷰를 저장하고 조회할 수 있다")
    void save() {
        // Arrange
        Member savedMember = memberRepository.findById(member.getMemberId()).get();
        Book savedBook = book; // 이미 생성된 book 사용

        Review newReview = Review.of(
                "Amazing insights.",
                new BigDecimal("5"),
                savedMember,
                savedBook
        );

        // Act
        Review savedReview = reviewRepository.save(newReview);
        entityManager.flush(); // 변경 사항을 데이터베이스에 반영

        // Assert
        assertThat(savedReview.getReviewId()).isNotNull();
        assertThat(savedReview.getReviewContent()).isEqualTo("Amazing insights.");
        assertThat(savedReview.getReviewRating()).isEqualByComparingTo(new BigDecimal("5"));
    }

    /**
     * deleteById 메서드 테스트
     */
    @Test
    @DisplayName("deleteById - 리뷰를 삭제할 수 있다")
    void deleteById() {
        // Arrange
        Long reviewId = 2L;

        // Act
        reviewRepository.deleteById(reviewId);
        entityManager.flush(); // 변경 사항을 데이터베이스에 반영

        // Assert
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        assertThat(reviewOpt).isNotPresent();
    }
}