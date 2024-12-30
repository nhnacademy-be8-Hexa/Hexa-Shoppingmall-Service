package com.nhnacademy.hexashoppingmallservice.repository.member;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberReport;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemberReportRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberReportRepository memberReportRepository;

    private String existingMemberId;
    private String nonExistingMemberId;
    private Long existingReviewId;
    private Long nonExistingReviewId;

    private Member member;
    private Review review;
    private Book book;
    private Rating rating;
    private MemberStatus memberStatus;
    private Publisher publisher;
    private BookStatus bookStatus;

    @BeforeEach
    void setUp() {
        existingMemberId = "member123";
        nonExistingMemberId = "member999";
        existingReviewId = 1L;
        nonExistingReviewId = 999L;

        // Rating 초기화 using 'of' method
        rating = Rating.of("Good", 80);
        // ratingId는 DB가 자동으로 생성하므로 설정하지 않습니다.

        // MemberStatus 초기화 using 'of' method
        memberStatus = MemberStatus.of("Active");
        // statusId는 DB가 자동으로 생성하므로 설정하지 않습니다.

        // Publisher 초기화 using 'of' method
        publisher = Publisher.of("Sample Publisher");
        // publisherId는 DB가 자동으로 생성하므로 설정하지 않습니다.

        // BookStatus 초기화 using 'of' method
        bookStatus = BookStatus.of("Available");
        // bookStatusId는 DB가 자동으로 생성하므로 설정하지 않습니다.

        // Member 초기화 using 'of' method
        member = Member.of(
                existingMemberId,
                "password123",
                "John Doe",
                "01012345678",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                rating,
                memberStatus
        );
        member.setMemberCreatedAt(LocalDateTime.now());

        // Book 초기화 using 'of' method
        book = Book.of(
                "Sample Book",
                "A sample book for testing.",
                LocalDate.of(2020, 1, 1),
                1234567890123L,
                20000,
                18000,
                publisher,
                bookStatus
        );

        // Review 초기화 using 'of' method
        review = Review.of(
                "Great book!",
                new BigDecimal("4.5"),
                member,
                book
        );

        // 엔티티들을 DB에 저장
        entityManager.persist(rating);
        entityManager.persist(memberStatus);
        entityManager.persist(publisher);
        entityManager.persist(bookStatus);
        entityManager.persist(member);
        entityManager.persist(book);
        entityManager.persist(review);
        entityManager.flush();

        // 설정된 IDs를 가져오기 위해 DB에서 조회
        existingReviewId = review.getReviewId(); // reviewId는 DB에서 생성됨
    }

    /**
     * MemberReport 초기화 using 'of' method
     */
    private MemberReport createMemberReport(Member member, Review review) {
        MemberReport memberReport = MemberReport.of(member, review);
        entityManager.persist(memberReport);
        entityManager.flush();
        return memberReport;
    }

    @Nested
    @DisplayName("countByReviewReviewId 메서드 테스트")
    class CountByReviewReviewIdTests {

        @Test
        @DisplayName("특정 리뷰에 대한 신고 수가 0일 때")
        void countByReviewReviewId_NoReports() {
            // Arrange
            // 특정 리뷰에 대한 신고가 없는 상태

            // Act
            Long count = memberReportRepository.countByReviewReviewId(existingReviewId);

            // Assert
            assertThat(count).isEqualTo(0L);
        }

        @Test
        @DisplayName("특정 리뷰에 대한 신고 수가 존재할 때")
        void countByReviewReviewId_WithReports() {
            // Arrange
            createMemberReport(member, review);
            createMemberReport(member, review);

            // Act
            Long count = memberReportRepository.countByReviewReviewId(existingReviewId);

            // Assert
            assertThat(count).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("deleteAllByReviewReviewId 메서드 테스트")
    class DeleteAllByReviewReviewIdTests {

        @Test
        @DisplayName("특정 리뷰에 대한 모든 신고를 삭제한다")
        void deleteAllByReviewReviewId_Success() {
            // Arrange
            MemberReport report1 = createMemberReport(member, review);
            MemberReport report2 = createMemberReport(member, review);

            // Act
            memberReportRepository.deleteAllByReviewReviewId(existingReviewId);
            entityManager.flush();

            // Assert
            Long count = memberReportRepository.countByReviewReviewId(existingReviewId);
            assertThat(count).isEqualTo(0L);
        }

        @Test
        @DisplayName("특정 리뷰에 대한 신고가 없을 때")
        void deleteAllByReviewReviewId_NoReports() {
            // Arrange
            // 특정 리뷰에 대한 신고가 없는 상태

            // Act
            memberReportRepository.deleteAllByReviewReviewId(existingReviewId);
            entityManager.flush();

            // Assert
            Long count = memberReportRepository.countByReviewReviewId(existingReviewId);
            assertThat(count).isEqualTo(0L);
        }
    }

    // existsByReviewReviewId 관련 테스트는 제거되었습니다.
}