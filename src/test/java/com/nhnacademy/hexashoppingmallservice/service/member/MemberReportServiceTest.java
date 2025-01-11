package com.nhnacademy.hexashoppingmallservice.service.member;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.member.*;
import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberReportAlreadyExist;
import com.nhnacademy.hexashoppingmallservice.exception.review.ReviewNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberReportRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.review.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MemberReportServiceTest {

    @Mock
    private MemberReportRepository memberReportRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private MemberReportService memberReportService;

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
    void setUp() throws Exception {
        existingMemberId = "member123";
        nonExistingMemberId = "member999";
        existingReviewId = 1L;
        nonExistingReviewId = 999L;

        // Rating 초기화 using 'of' method
        rating = Rating.of("Good", 80);
        rating.setRatingId(1L); // 리플렉션 없이 setter 사용 가능

        // MemberStatus 초기화 using 'of' method
        memberStatus = MemberStatus.of("Active");
        memberStatus.setStatusId(1L); // 리플렉션 없이 setter 사용 가능

        // Publisher 초기화 using 'of' method
        publisher = Publisher.of("Sample Publisher");
        setField(publisher, "publisherId", 1L); // 리플렉션으로 설정

        // BookStatus 초기화 using 'of' method
        bookStatus = BookStatus.of("Available");
        setField(bookStatus, "bookStatusId", 1L); // 리플렉션으로 설정

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
        setField(book, "bookId", 1L); // 리플렉션으로 설정

        // Review 초기화 using 'of' method
        review = Review.of(
                "Great book!",
                new BigDecimal("4.5"),
                member,
                book
        );
        setField(review, "reviewId", existingReviewId); // 리플렉션으로 설정
    }

    /**
     * 리플렉션을 사용하여 프라이빗 필드에 값을 설정합니다.
     *
     * @param target    값을 설정할 객체
     * @param fieldName 설정할 필드 이름
     * @param value     설정할 값
     * @throws Exception 리플렉션 관련 예외
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Nested
    @DisplayName("saveMemberReport 메서드 테스트")
    class SaveMemberReportTests {

        @Test
        @DisplayName("성공적으로 신고를 저장한다")
        void saveMemberReport_Success() throws Exception {
            // Arrange
            when(memberRepository.existsById(existingMemberId)).thenReturn(true);
            when(reviewRepository.existsById(existingReviewId)).thenReturn(true);
            when(memberReportRepository.countByReviewReviewId(existingReviewId)).thenReturn(0L);
            when(memberRepository.findById(existingMemberId)).thenReturn(Optional.of(member));
            when(reviewRepository.findById(existingReviewId)).thenReturn(Optional.of(review));

            // MemberReport 초기화 using 'of' method
            MemberReport savedReport = MemberReport.of(member, review);
            setField(savedReport, "memberReportId", 1L); // 리플렉션으로 설정
            when(memberReportRepository.save(any(MemberReport.class))).thenReturn(savedReport);

            // Act
            memberReportService.saveMemberReport(existingMemberId, existingReviewId);

            // Assert
            verify(memberRepository, times(1)).existsById(existingMemberId);
            verify(reviewRepository, times(1)).existsById(existingReviewId);
            verify(memberReportRepository, times(2)).countByReviewReviewId(existingReviewId);
            verify(memberRepository, times(1)).findById(existingMemberId);
            verify(reviewRepository, times(1)).findById(existingReviewId);
            verify(memberReportRepository, times(1)).save(any(MemberReport.class));
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID일 때 예외를 던진다")
        void saveMemberReport_MemberNotFound() {
            // Arrange
            when(memberRepository.existsById(nonExistingMemberId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> memberReportService.saveMemberReport(nonExistingMemberId, existingReviewId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessageContaining(String.format("Member ID %s does Not Exist!", nonExistingMemberId));

            verify(memberRepository, times(1)).existsById(nonExistingMemberId);
            verify(reviewRepository, never()).existsById(anyLong());
            verify(memberReportRepository, never()).countByReviewReviewId(anyLong());
            verify(memberRepository, never()).findById(anyString());
            verify(reviewRepository, never()).findById(anyLong());
            verify(memberReportRepository, never()).save(any(MemberReport.class));
        }

        @Test
        @DisplayName("존재하지 않는 리뷰 ID일 때 예외를 던진다")
        void saveMemberReport_ReviewNotFound() {
            // Arrange
            when(memberRepository.existsById(existingMemberId)).thenReturn(true);
            when(reviewRepository.existsById(nonExistingReviewId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> memberReportService.saveMemberReport(existingMemberId, nonExistingReviewId))
                    .isInstanceOf(ReviewNotFoundException.class)
                    .hasMessageContaining(String.format("Review ID %d does Not Exist!", nonExistingReviewId));

            verify(memberRepository, times(1)).existsById(existingMemberId);
            verify(reviewRepository, times(1)).existsById(nonExistingReviewId);
            verify(memberReportRepository, never()).countByReviewReviewId(anyLong());
            verify(memberRepository, never()).findById(anyString());
            verify(reviewRepository, never()).findById(anyLong());
            verify(memberReportRepository, never()).save(any(MemberReport.class));
        }

        @Test
        @DisplayName("이미 신고가 존재할 때 예외를 던진다")
        void saveMemberReport_ReportAlreadyExists() {
            // Arrange
            when(memberRepository.existsById(existingMemberId)).thenReturn(true);
            when(reviewRepository.existsById(existingReviewId)).thenReturn(true);
            when(memberReportRepository.countByReviewReviewId(existingReviewId)).thenReturn(1L);
            when(memberReportRepository.countByMemberMemberId(existingMemberId)).thenReturn(1L);  // 추가

            // Act & Assert
            assertThatThrownBy(() -> memberReportService.saveMemberReport(existingMemberId, existingReviewId))
                    .isInstanceOf(MemberReportAlreadyExist.class)
                    .hasMessageContaining("Member report already exist!");

            verify(memberRepository, times(1)).existsById(existingMemberId);
            verify(reviewRepository, times(1)).existsById(existingReviewId);
            verify(memberReportRepository, times(1)).countByReviewReviewId(existingReviewId);
            verify(memberReportRepository, times(1)).countByMemberMemberId(existingMemberId);  // 추가
            verify(memberRepository, never()).findById(anyString());
            verify(reviewRepository, never()).findById(anyLong());
            verify(memberReportRepository, never()).save(any(MemberReport.class));
        }
    }

    @Nested
    @DisplayName("totalReport 메서드 테스트")
    class TotalReportTests {

        @Test
        @DisplayName("특정 리뷰의 신고 총계를 성공적으로 조회한다")
        void totalReport_Success() {
            // Arrange
            when(reviewRepository.existsById(existingReviewId)).thenReturn(true);
            when(memberReportRepository.countByReviewReviewId(existingReviewId)).thenReturn(5L);

            // Act
            Long result = memberReportService.totalReport(existingReviewId);

            // Assert
            assertThat(result).isEqualTo(5L);
            verify(reviewRepository, times(1)).existsById(existingReviewId);
            verify(memberReportRepository, times(1)).countByReviewReviewId(existingReviewId);
        }

        @Test
        @DisplayName("존재하지 않는 리뷰 ID일 때 예외를 던진다")
        void totalReport_ReviewNotFound() {
            // Arrange
            when(reviewRepository.existsById(nonExistingReviewId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> memberReportService.totalReport(nonExistingReviewId))
                    .isInstanceOf(ReviewNotFoundException.class)
                    .hasMessageContaining(String.format("Review ID %d does Not Exist!", nonExistingReviewId));

            verify(reviewRepository, times(1)).existsById(nonExistingReviewId);
            verify(memberReportRepository, never()).countByReviewReviewId(anyLong());
        }
    }

    @Nested
    @DisplayName("allDelete 메서드 테스트")
    class AllDeleteTests {

        @Test
        @DisplayName("특정 리뷰의 모든 신고를 성공적으로 삭제한다")
        void allDelete_Success() {
            // Arrange
            when(reviewRepository.existsById(existingReviewId)).thenReturn(true);
            doNothing().when(memberReportRepository).deleteAllByReviewReviewId(existingReviewId);

            // 더미 Review 객체 생성 (필요한 경우 필드 설정)
            Review dummyReview = new Review();
            // 예를 들어, 리뷰가 차단된 상태에서 삭제 후 차단 해제를 수행한다고 가정하면:
            dummyReview.setReviewIsBlocked(true);

            when(reviewRepository.findById(existingReviewId)).thenReturn(Optional.of(dummyReview));

            memberReportService.allDelete(existingReviewId);

            verify(reviewRepository, times(1)).existsById(existingReviewId);
            verify(memberReportRepository, times(1)).deleteAllByReviewReviewId(existingReviewId);
            verify(reviewRepository, times(1)).findById(existingReviewId);
        }

        @Test
        @DisplayName("존재하지 않는 리뷰 ID일 때 예외를 던진다")
        void allDelete_ReviewNotFound() {
            // Arrange
            when(reviewRepository.existsById(nonExistingReviewId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> memberReportService.allDelete(nonExistingReviewId))
                    .isInstanceOf(ReviewNotFoundException.class)
                    .hasMessageContaining(String.format("Review ID %d does Not Exist!", nonExistingReviewId));

            verify(reviewRepository, times(1)).existsById(nonExistingReviewId);
            verify(memberReportRepository, never()).deleteAllByReviewReviewId(anyLong());
        }
    }
}