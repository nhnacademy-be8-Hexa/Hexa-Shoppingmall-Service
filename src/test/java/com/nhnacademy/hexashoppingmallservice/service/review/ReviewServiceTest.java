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
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.review.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Captor
    private ArgumentCaptor<Review> reviewCaptor;

    private ReviewRequestDTO validReviewRequestDTO;
    private String validMemberId;
    private Long validBookId;
    private Member validMember;
    private Book validBook;
    private Review validReview;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        validReviewRequestDTO = new ReviewRequestDTO("Great book!", new BigDecimal("5"));
        validMemberId = "member123";
        validBookId = 1L;

        // Mock Member
        validMember = Member.of(
                validMemberId,
                "password123",
                "John Doe",
                "01012345678",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                null,
                null
        );
        validMember.setMemberId(validMemberId);

        // Mock Book
        validBook = new Book();
        Field validBookField = validBook.getClass().getDeclaredField("bookId");
        validBookField.setAccessible(true);
        validBookField.set(validBook, validBookId);

        // Mock Review
        validReview = Review.of(validReviewRequestDTO.getReviewContent(),
                validReviewRequestDTO.getReviewRating(),
                validMember,
                validBook);
        Field validReviewField = validReview.getClass().getDeclaredField("reviewId");
        validReviewField.setAccessible(true);
        validReviewField.set(validReview, 1L);
    }

    /**
     * 리뷰 생성 성공 시나리오 테스트
     */
    @Test
    @DisplayName("createReview - 성공적으로 리뷰를 생성한다")
    void createReview_Success() {
        // Arrange
        when(memberRepository.existsById(validMemberId)).thenReturn(true);
        when(bookRepository.existsById(validBookId)).thenReturn(true);
        when(memberRepository.findById(validMemberId)).thenReturn(Optional.of(validMember));
        when(bookRepository.findById(validBookId)).thenReturn(Optional.of(validBook));

        // Act
        reviewService.createReview(validReviewRequestDTO, validMemberId, validBookId);

        // Assert
        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(bookRepository, times(1)).existsById(validBookId);
        verify(memberRepository, times(1)).findById(validMemberId);
        verify(bookRepository, times(1)).findById(validBookId);
        verify(reviewRepository, times(1)).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();
        assertEquals(validReviewRequestDTO.getReviewContent(), savedReview.getReviewContent());
        assertEquals(validReviewRequestDTO.getReviewRating(), savedReview.getReviewRating());
        assertEquals(validMember, savedReview.getMember());
        assertEquals(validBook, savedReview.getBook());
    }

    /**
     * 리뷰 생성 시 회원이 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("createReview - 존재하지 않는 회원 ID로 리뷰 생성 시 MemberNotFoundException 발생")
    void createReview_MemberNotFound() {
        // Arrange
        when(memberRepository.existsById(validMemberId)).thenReturn(false);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () ->
                reviewService.createReview(validReviewRequestDTO, validMemberId, validBookId));

        assertEquals("Member ID member123 is not Found!", exception.getMessage());

        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(bookRepository, never()).existsById(anyLong());
    }

    /**
     * 리뷰 생성 시 책이 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("createReview - 존재하지 않는 책 ID로 리뷰 생성 시 BookNotFoundException 발생")
    void createReview_BookNotFound() {
        // Arrange
        when(memberRepository.existsById(validMemberId)).thenReturn(true);
        when(bookRepository.existsById(validBookId)).thenReturn(false);

        // Act & Assert
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                reviewService.createReview(validReviewRequestDTO, validMemberId, validBookId));

        assertEquals("Book ID 1 is not Found!", exception.getMessage());

        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(bookRepository, times(1)).existsById(validBookId);
        verify(memberRepository, never()).findById(anyString());
        verify(bookRepository, never()).findById(anyLong());
    }

    /**
     * 특정 회원의 리뷰 목록 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getReviewsFromMember - 특정 회원의 리뷰 목록을 성공적으로 조회한다")
    void getReviewsFromMember_Success() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        when(memberRepository.existsById(validMemberId)).thenReturn(true);

        ReviewProjection reviewProjection1 = mock(ReviewProjection.class);
        ReviewProjection reviewProjection2 = mock(ReviewProjection.class);
        List<ReviewProjection> mockReviews = Arrays.asList(reviewProjection1, reviewProjection2);

        when(reviewRepository.findByMemberMemberIdAndReviewIsBlockedFalse(validMemberId, pageable))
                .thenReturn(new PageImpl<>(mockReviews));

        // Act
        List<ReviewProjection> result = reviewService.getReviewsFromMember(pageable, validMemberId);

        // Assert
        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(reviewRepository, times(1)).findByMemberMemberIdAndReviewIsBlockedFalse(validMemberId, pageable);
        assertEquals(mockReviews, result);
    }

    /**
     * 특정 회원의 리뷰 목록 조회 시 회원이 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("getReviewsFromMember - 존재하지 않는 회원 ID로 리뷰 목록 조회 시 MemberNotFoundException 발생")
    void getReviewsFromMember_MemberNotFound() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        when(memberRepository.existsById(validMemberId)).thenReturn(false);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () ->
                reviewService.getReviewsFromMember(pageable, validMemberId));

        assertEquals("Member ID member123 is not Found!", exception.getMessage());

        verify(memberRepository, times(1)).existsById(validMemberId);
//        verify(reviewRepository, never()).findByMemberMemberIdAndReviewIsblockedFalse(validMemberId, pageable);
    }

    /**
     * 특정 책의 리뷰 목록 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getReviewsFromBook - 특정 책의 리뷰 목록을 성공적으로 조회한다")
    void getReviewsFromBook_Success() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        when(bookRepository.existsById(validBookId)).thenReturn(true);

        ReviewProjection reviewProjection1 = mock(ReviewProjection.class);
        ReviewProjection reviewProjection2 = mock(ReviewProjection.class);
        List<ReviewProjection> mockReviews = Arrays.asList(reviewProjection1, reviewProjection2);

        when(reviewRepository.findByBookBookIdAndReviewIsBlockedFalse(validBookId, pageable))
                .thenReturn(new PageImpl<>(mockReviews));

        // Act
        List<ReviewProjection> result = reviewService.getReviewsFromBook(pageable, validBookId);

        // Assert
        verify(bookRepository, times(1)).existsById(validBookId);
        verify(reviewRepository, times(1)).findByBookBookIdAndReviewIsBlockedFalse(validBookId, pageable);
        assertEquals(mockReviews, result);
    }

    /**
     * 특정 책의 리뷰 목록 조회 시 책이 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("getReviewsFromBook - 존재하지 않는 책 ID로 리뷰 목록 조회 시 BookNotFoundException 발생")
    void getReviewsFromBook_BookNotFound() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        when(bookRepository.existsById(validBookId)).thenReturn(false);

        // Act & Assert
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                reviewService.getReviewsFromBook(pageable, validBookId));

        assertEquals("Book ID 1 is not Found!", exception.getMessage());

        verify(bookRepository, times(1)).existsById(validBookId);
//        verify(reviewRepository, never()).findByBookBookIdAndReviewIsblockedFalse(anyLong(), pageable);
    }

    /**
     * 리뷰 업데이트 성공 시나리오 테스트
     */
    @Test
    @DisplayName("updateReview - 리뷰를 성공적으로 업데이트한다")
    void updateReview_Success() {
        // Arrange
        Long reviewId = 1L;
        ReviewRequestDTO updateDTO = new ReviewRequestDTO("Updated content", new BigDecimal("4"));
        when(reviewRepository.existsById(reviewId)).thenReturn(true);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(validReview));

        // Act
        reviewService.updateReview(updateDTO, reviewId);

        // Assert
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, times(1)).findById(reviewId);
        assertEquals(updateDTO.getReviewContent(), validReview.getReviewContent());
        assertEquals(updateDTO.getReviewRating(), validReview.getReviewRating());
    }

    /**
     * 리뷰 업데이트 시 리뷰가 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("updateReview - 존재하지 않는 리뷰 ID로 업데이트 시 ReviewNotFoundException 발생")
    void updateReview_ReviewNotFound() {
        // Arrange
        Long reviewId = 1L;
        ReviewRequestDTO updateDTO = new ReviewRequestDTO("Updated content", new BigDecimal("4"));
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        // Act & Assert
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () ->
                reviewService.updateReview(updateDTO, reviewId));

        assertEquals("Review ID 1 is not Found!", exception.getMessage());

        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, never()).findById(anyLong());
    }

    /**
     * 리뷰 삭제 성공 시나리오 테스트
     */
    @Test
    @DisplayName("deleteReview - 리뷰를 성공적으로 삭제한다")
    void deleteReview_Success() {
        // Arrange
        Long reviewId = 1L;
        doNothing().when(reviewRepository).deleteById(reviewId);

        // Act
        reviewService.deleteReview(reviewId);

        // Assert
        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    /**
     * 리뷰 삭제 시 리뷰가 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("deleteReview - 존재하지 않는 리뷰 ID로 삭제 시 EmptyResultDataAccessException 발생")
    void deleteReview_ReviewNotFound() {
        // Arrange
        Long reviewId = 1L;
        doThrow(new EmptyResultDataAccessException(1)).when(reviewRepository).deleteById(reviewId);

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () ->
                reviewService.deleteReview(reviewId));

        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    /**
     * 차단된 리뷰 목록 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getReviewsIsBlocked - 차단된 리뷰 목록을 성공적으로 조회한다")
    void getReviewsIsBlocked_Success() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        ReviewProjection reviewProjection1 = mock(ReviewProjection.class);
        ReviewProjection reviewProjection2 = mock(ReviewProjection.class);
        List<ReviewProjection> mockReviews = Arrays.asList(reviewProjection1, reviewProjection2);

        when(reviewRepository.findAllByReviewIsBlockedTrue(pageable))
                .thenReturn(new PageImpl<>(mockReviews));

        // Act
        List<ReviewProjection> result = reviewService.getReviewsIsBlocked(pageable);

        // Assert
        verify(reviewRepository, times(1)).findAllByReviewIsBlockedTrue(pageable);
        assertEquals(mockReviews, result);
    }

    /**
     * 리뷰 차단 상태 업데이트 성공 시나리오 테스트
     */
    @Test
    @DisplayName("updateBlocked - 리뷰의 차단 상태를 성공적으로 업데이트한다")
    void updateBlocked_Success() {
        // Arrange
        Long reviewId = 1L;
        Boolean blocked = true;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(validReview));

        // Act
        reviewService.updateBlocked(reviewId, blocked);

        // Assert
        verify(reviewRepository, times(1)).findById(reviewId);
        assertEquals(blocked, validReview.isReviewIsBlocked());
    }

    /**
     * 리뷰 차단 상태 업데이트 시 리뷰가 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("updateBlocked - 존재하지 않는 리뷰 ID로 차단 상태 업데이트 시 ReviewNotFoundException 발생")
    void updateBlocked_ReviewNotFound() {
        // Arrange
        Long reviewId = 1L;
        Boolean blocked = true;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () ->
                reviewService.updateBlocked(reviewId, blocked));

        assertEquals("Review ID 1 is not Found!", exception.getMessage());

        verify(reviewRepository, times(1)).findById(reviewId);
    }

    /**
     * 특정 회원의 리뷰 총 개수 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getReviewsFromMemberTotal - 특정 회원의 리뷰 총 개수를 성공적으로 조회한다")
    void getReviewsFromMemberTotal_Success() {
        // Arrange
        when(memberRepository.existsById(validMemberId)).thenReturn(true);
        when(reviewRepository.countByMemberMemberIdAndReviewIsBlockedFalse(validMemberId))
                .thenReturn(5L);

        // Act
        Long result = reviewService.getReviewsFromMemberTotal(validMemberId);

        // Assert
        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(reviewRepository, times(1)).countByMemberMemberIdAndReviewIsBlockedFalse(validMemberId);
        assertEquals(5L, result);
    }

    /**
     * 특정 회원의 리뷰 총 개수 조회 시 회원이 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("getReviewsFromMemberTotal - 존재하지 않는 회원 ID로 총 개수 조회 시 MemberNotFoundException 발생")
    void getReviewsFromMemberTotal_MemberNotFound() {
        // Arrange
        when(memberRepository.existsById(validMemberId)).thenReturn(false);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () ->
                reviewService.getReviewsFromMemberTotal(validMemberId));

        assertEquals("Member ID member123 is not Found!", exception.getMessage());
        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(reviewRepository, never()).countByMemberMemberIdAndReviewIsBlockedFalse(validMemberId);
    }

    /**
     * 특정 책의 리뷰 총 개수 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getReviewsFromBookTotal - 특정 책의 리뷰 총 개수를 성공적으로 조회한다")
    void getReviewsFromBookTotal_Success() {
        // Arrange
        when(bookRepository.existsById(validBookId)).thenReturn(true);
        when(reviewRepository.countByBookBookIdAndReviewIsBlockedFalse(validBookId))
                .thenReturn(3L);

        // Act
        Long result = reviewService.getReviewsFromBookTotal(validBookId);

        // Assert
        verify(bookRepository, times(1)).existsById(validBookId);
        verify(reviewRepository, times(1)).countByBookBookIdAndReviewIsBlockedFalse(validBookId);
        assertEquals(3L, result);
    }

    /**
     * 특정 책의 리뷰 총 개수 조회 시 책이 존재하지 않을 때 예외 발생 테스트
     */
    @Test
    @DisplayName("getReviewsFromBookTotal - 존재하지 않는 책 ID로 총 개수 조회 시 BookNotFoundException 발생")
    void getReviewsFromBookTotal_BookNotFound() {
        // Arrange
        when(bookRepository.existsById(validBookId)).thenReturn(false);

        // Act & Assert
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                reviewService.getReviewsFromBookTotal(validBookId));

        assertEquals("Book ID 1 is not Found!", exception.getMessage());
        verify(bookRepository, times(1)).existsById(validBookId);
        verify(reviewRepository, never()).countByBookBookIdAndReviewIsBlockedFalse(validBookId);
    }

    /**
     * 회원과 책에 대한 리뷰 존재 여부 체크 성공 시나리오 테스트
     */
    @Test
    @DisplayName("checkReviews - 특정 회원이 특정 책에 대해 리뷰를 남긴 적이 있는지 체크한다")
    void checkReviews_Success() {
        // Arrange
        when(memberRepository.existsById(validMemberId)).thenReturn(true);
        when(bookRepository.existsById(validBookId)).thenReturn(true);
        when(reviewRepository.existsByMemberMemberIdAndBookBookId(validMemberId, validBookId)).thenReturn(true);

        // Act
        boolean result = reviewService.checkReviews(validMemberId, validBookId);

        // Assert
        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(bookRepository, times(1)).existsById(validBookId);
        verify(reviewRepository, times(1)).existsByMemberMemberIdAndBookBookId(validMemberId, validBookId);
        assertTrue(result);
    }

    /**
     * 회원이나 책이 존재하지 않으면 리뷰가 없는 경우
     */
    @Test
    @DisplayName("checkReviews - 존재하지 않는 회원이나 책에 대해 리뷰를 체크 시 예외 발생")
    void checkReviews_NotFound() {
        // Arrange
        lenient().when(memberRepository.existsById(validMemberId)).thenReturn(false);
        lenient().when(bookRepository.existsById(validBookId)).thenReturn(true);

        // Act & Assert
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () ->
                reviewService.checkReviews(validMemberId, validBookId));

        assertEquals("Member ID member123 is not Found!", exception.getMessage());
        verify(memberRepository, times(1)).existsById(validMemberId);
        verify(bookRepository, never()).existsById(validBookId);
        verify(reviewRepository, never()).existsByMemberMemberIdAndBookBookId(validMemberId, validBookId);
    }

    /**
     * 신고가 5회 이상인 리뷰 목록 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getHighlyReportedReviews - 신고가 5회 이상인 리뷰 목록을 성공적으로 조회한다")
    void getHighlyReportedReviews_Success() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        ReviewProjection reviewProjection1 = mock(ReviewProjection.class);
        ReviewProjection reviewProjection2 = mock(ReviewProjection.class);
        List<ReviewProjection> mockReviews = Arrays.asList(reviewProjection1, reviewProjection2);

        when(reviewRepository.findReviewsWithMinReports(5L, pageable))
                .thenReturn(new PageImpl<>(mockReviews));

        // Act
        List<ReviewProjection> result = reviewService.getHighlyReportedReviews(pageable);

        // Assert
        verify(reviewRepository, times(1)).findReviewsWithMinReports(5L, pageable);
        assertEquals(mockReviews, result);
    }

    /**
     * 신고가 5회 이상인 리뷰 총 개수 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getTotalHighlyReportedReviews - 신고가 5회 이상인 리뷰 총 개수를 성공적으로 조회한다")
    void getTotalHighlyReportedReviews_Success() {
        // Arrange
        when(reviewRepository.countReviewsWithMinReports(5L)).thenReturn(10L);

        // Act
        long result = reviewService.getTotalHighlyReportedReviews();

        // Assert
        verify(reviewRepository, times(1)).countReviewsWithMinReports(5L);
        assertEquals(10L, result);
    }


    /**
     * 특정 책에 대한 평균 리뷰 평점 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getAverageReviewRatingByBookId - 특정 책에 대한 평균 리뷰 평점을 성공적으로 조회한다")
    void getAverageReviewRatingByBookId_Success() {
        // Arrange
        Long bookId = 1L;
        BigDecimal expectedAverageRating = new BigDecimal("4.5");
        when(reviewRepository.findAverageReviewRatingByBookId(bookId)).thenReturn(expectedAverageRating);

        // Act
        BigDecimal result = reviewService.getAverageReviewRatingByBookId(bookId);

        // Assert
        verify(reviewRepository, times(1)).findAverageReviewRatingByBookId(bookId);
        assertEquals(expectedAverageRating, result);
    }

    /**
     * 특정 리뷰 ID로 리뷰 조회 시 리뷰가 존재하지 않으면 예외 발생 테스트
     */
    @Test
    @DisplayName("getReviewById - 존재하지 않는 리뷰 ID로 조회 시 ReviewNotFoundException 발생")
    void getReviewById_ReviewNotFound() {
        // Arrange
        Long reviewId = 1L;
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        // Act & Assert
        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () ->
                reviewService.getReviewById(reviewId));

        assertEquals("Review ID 1 is not Found!", exception.getMessage());
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, never()).findByReviewId(reviewId);
    }

    /**
     * 특정 리뷰 ID로 리뷰 조회 성공 시나리오 테스트
     */
    @Test
    @DisplayName("getReviewById - 특정 리뷰 ID로 리뷰를 성공적으로 조회한다")
    void getReviewById_Success() {
        // Arrange
        Long reviewId = 1L;
        ReviewProjection expectedReviewProjection = mock(ReviewProjection.class);
        when(reviewRepository.existsById(reviewId)).thenReturn(true);
        when(reviewRepository.findByReviewId(reviewId)).thenReturn(expectedReviewProjection);

        // Act
        ReviewProjection result = reviewService.getReviewById(reviewId);

        // Assert
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, times(1)).findByReviewId(reviewId);
        assertEquals(expectedReviewProjection, result);
    }
}