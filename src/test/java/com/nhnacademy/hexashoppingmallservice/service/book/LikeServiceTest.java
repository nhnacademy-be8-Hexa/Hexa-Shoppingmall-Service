package com.nhnacademy.hexashoppingmallservice.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Like;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotExistException;
import com.nhnacademy.hexashoppingmallservice.exception.book.LikeAlreadyExistsException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.LikeRepository;

import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;


    @InjectMocks
    private LikeService likeService;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        Publisher publisher = Publisher.of("PublisherName");
        BookStatus bookStatus = BookStatus.of("Available");
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

        Field bookIdField = book.getClass().getDeclaredField("bookId");
        bookIdField.setAccessible(true);
        bookIdField.set(book, 1L);
        member = Member.of(
                "123",
                "password",
                "John Doe",
                "1234567890",
                "test@test.com",
                LocalDate.of(1990, 1, 1),
                Rating.of("Gold", 10),
                MemberStatus.builder().statusName("Active").build()
        );

    }

    @Test
    void createLike_whenBookDoesNotExist_shouldThrowException() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(BookNotExistException.class, () ->
                likeService.createLike(1L, "123"));
    }

    @Test
    void testCreateLike_whenMemberDoesNotExist_shouldThrowException() {

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(false);
        Assertions.assertThrows(MemberNotFoundException.class, () ->
                likeService.createLike(1L, "123"));

    }

    @Test
    void testCreateLike_whenLikeAlreadyExists_shouldThrowException() {

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(true);
        Assertions.assertThrows(LikeAlreadyExistsException.class, () ->
                likeService.createLike(1L, "123"));
    }

    @Test
    void testCreateLike_shouldSaveLike() {

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        likeService.createLike(1L, "123");

        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void testSumLikes_whenBookDoesNotExist_shouldThrowException() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(BookNotExistException.class, () ->
                likeService.sumLikes(1L));
    }

    @Test
    void testSumLikes_shouldReturnLikeCount() {

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(likeRepository.countByBookBookId(1L)).thenReturn(5L);

        Long likeCount = likeService.sumLikes(1L);

        verify(likeRepository).countByBookBookId(1L);
        assertEquals(5L, likeCount);
    }

    @Test
    void testGetBooksLikedByMember_whenMemberDoesNotExist_shouldThrowException() {
        when(memberRepository.existsById("123")).thenReturn(false);
        Assertions.assertThrows(MemberNotFoundException.class, () ->
                likeService.getBooksLikedByMember("123"));
    }

    @Test
    void testGetBooksLikedByMember_shouldReturnBooks() {
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.findBooksLikedByMemberId("123")).thenReturn(List.of(book));

        List<Book> likedBooks = likeService.getBooksLikedByMember("123");
        verify(likeRepository).findBooksLikedByMemberId("123");

        assertEquals(1, likedBooks.size());
    }

    @Test
    void testToggleLike_whenLikeDoesNotExist_shouldCreateLike() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById("123")).thenReturn(Optional.of(member));

        // When
        likeService.toggleLike(1L, "123");

        // Then
        verify(likeRepository).save(any(Like.class));  // Like should be created
    }

    @Test
    void testToggleLike_whenLikeAlreadyExists_shouldDeleteLike() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(true);

        // When
        likeService.toggleLike(1L, "123");

        // Then
        verify(likeRepository).deleteByBookBookIdAndMemberMemberId(1L, "123");  // Like should be deleted
    }

    @Test
    void testDeleteLike_whenLikeDoesNotExist_shouldDoNothing() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(false);

        // When
        likeService.deleteLike(1L, "123");

        // Then
        verify(likeRepository, times(1)).deleteByBookBookIdAndMemberMemberId(any(), any());
    }

    @Test
    void testDeleteLike_whenLikeExists_shouldDeleteLike() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(true);

        // When
        likeService.deleteLike(1L, "123");

        // Then
        verify(likeRepository).deleteByBookBookIdAndMemberMemberId(1L, "123");  // Like should be deleted
    }


    @Test
    void testHasLiked_whenBookDoesNotExist_shouldThrowException() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(BookNotExistException.class, () ->
                likeService.hasLiked(1L, "123"));
    }

    @Test
    void testHasLiked_whenMemberDoesNotExist_shouldThrowException() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(false);
        Assertions.assertThrows(MemberNotFoundException.class, () ->
                likeService.hasLiked(1L, "123"));
    }

    @Test
    void testHasLiked_whenLikeExists_shouldReturnTrue() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(true);

        Boolean hasLiked = likeService.hasLiked(1L, "123");

        assertEquals(true, hasLiked);
        verify(likeRepository).existsByBookBookIdAndMemberMemberId(1L, "123");
    }

    @Test
    void testHasLiked_whenLikeDoesNotExist_shouldReturnFalse() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById("123")).thenReturn(true);
        when(likeRepository.existsByBookBookIdAndMemberMemberId(1L, "123")).thenReturn(false);

        Boolean hasLiked = likeService.hasLiked(1L, "123");

        assertEquals(false, hasLiked);
        verify(likeRepository).existsByBookBookIdAndMemberMemberId(1L, "123");
    }
}
