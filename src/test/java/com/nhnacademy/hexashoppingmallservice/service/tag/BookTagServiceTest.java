package com.nhnacademy.hexashoppingmallservice.service.tag;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookTag;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.exception.tag.AlreadyExistsBookTagException;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.BookTagRepository;
import com.nhnacademy.hexashoppingmallservice.repository.tag.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookTagServiceTest {

    @InjectMocks
    private BookTagService bookTagService;

    @Mock
    private BookTagRepository bookTagRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BookRepository bookRepository;


    @Test
    @DisplayName("책 아이디로 태그 리스트 조회")
    void getTagsByBookId() {
        Long bookId = 1L;
        Tag tag = Tag.of("Test Tag");
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookTagRepository.findTagsByBookId(bookId)).thenReturn(List.of(tag));

        List<Tag> tags = bookTagService.getTagsByBookId(bookId);

        assertThat(tags).isNotEmpty();
        assertThat(tags.get(0).getTagName()).isEqualTo("Test Tag");
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookTagRepository, times(1)).findTagsByBookId(bookId);
    }

    @Test
    @DisplayName("태그 아이디로 책 리스트 조회")
    void getBooksByTagId() {
        Long tagId = 1L;
        Book book = Book.of("Test Book", "Test Description", null, 1234567890123L, 10000, 9000, null, null);
        Pageable pageable = Pageable.unpaged();
        Page<Book> page = mock(Page.class);
        when(tagRepository.existsById(tagId)).thenReturn(true);
        when(bookTagRepository.findBooksByTagId(tagId, pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(List.of(book));

        List<Book> books = bookTagService.getBooksByTagId(tagId, pageable);

        assertThat(books).isNotEmpty();
        assertThat(books.get(0).getBookTitle()).isEqualTo("Test Book");
        verify(tagRepository, times(1)).existsById(tagId);
        verify(bookTagRepository, times(1)).findBooksByTagId(tagId, pageable);
    }

    @Test
    @DisplayName("책과 태그 관계 생성")
    void createBookTag() {
        Long bookId = 1L;
        Long tagId = 1L;
        Book book = mock(Book.class);
        Tag tag = mock(Tag.class);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsByBook_BookIdAndTag_TagId(bookId, tagId)).thenReturn(false);

        bookTagService.create(bookId, tagId);

        verify(bookRepository, times(1)).findById(bookId);
        verify(tagRepository, times(1)).findById(tagId);
        verify(bookTagRepository, times(1)).existsByBook_BookIdAndTag_TagId(bookId, tagId);
        verify(bookTagRepository, times(1)).save(any(BookTag.class));
    }

    @Test
    @DisplayName("책과 태그 관계 생성 - 중복 예외")
    void createBookTagAlreadyExists() {
        Long bookId = 1L;
        Long tagId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mock(Book.class)));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(mock(Tag.class)));
        when(bookTagRepository.existsByBook_BookIdAndTag_TagId(bookId, tagId)).thenReturn(true);

        assertThatThrownBy(() -> bookTagService.create(bookId, tagId))
                .isInstanceOf(AlreadyExistsBookTagException.class)
                .hasMessageContaining("already exists");

        verify(bookTagRepository, times(1)).existsByBook_BookIdAndTag_TagId(bookId, tagId);
    }

    @Test
    @DisplayName("책과 태그 관계 삭제")
    void deleteBookTag() {
        Long bookId = 1L;
        Long tagId = 1L;

        doNothing().when(bookTagRepository).deleteByBook_BookIdAndTag_TagId(bookId, tagId);

        bookTagService.delete(bookId, tagId);

        verify(bookTagRepository, times(1)).deleteByBook_BookIdAndTag_TagId(bookId, tagId);
    }
}
