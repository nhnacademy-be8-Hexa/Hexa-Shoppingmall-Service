package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private BookStatusRepository bookStatusRepository;

    @InjectMocks
    private BookService bookService;

    private BookRequestDTO bookRequestDTO;
    private BookUpdateRequestDTO bookUpdateRequestDTO;
    private Book book;
    private Publisher publisher;
    private BookStatus bookStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        publisher = Publisher.of("PublisherName");
        bookStatus = BookStatus.of("Available");

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

        bookRequestDTO = new BookRequestDTO(
                "Book Title",
                "Book Description",
                LocalDate.now(),
                1234567890123L,
                20000,
                18000,
                true,
                "1",
                "1"
        );

        bookUpdateRequestDTO = new BookUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                15000,
                false,
                "1"
        );
    }

    @Test
    void createBook_Success() {
        when(publisherRepository.existsById(1L)).thenReturn(true);
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(bookStatusRepository.existsById(1L)).thenReturn(true);
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.of(bookStatus));
        when(bookRepository.existsByBookIsbn(1234567890123L)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook(bookRequestDTO);

        assertThat(createdBook.getBookTitle()).isEqualTo("Book Title");
        assertThat(createdBook.getPublisher().getPublisherName()).isEqualTo("PublisherName");
        assertThat(createdBook.getBookStatus().getBookStatus()).isEqualTo("Available");

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_PublisherNotFound() {
        when(publisherRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookService.createBook(bookRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("publisher id is not found 1");
    }

    @Test
    void updateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.of(bookStatus));
        when(bookRepository.save(book)).thenReturn(book);

        Book updatedBook = bookService.updateBook(1L, bookUpdateRequestDTO);

        assertThat(updatedBook.getBookTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getBookDescription()).isEqualTo("Updated Description");
        assertThat(updatedBook.getBookPrice()).isEqualTo(15000);
        assertThat(updatedBook.getBookWrappable()).isFalse();

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }


    @Test
    void updateBook_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookService.updateBook(1L, bookUpdateRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("bookId cannot found: 1");
    }

    @Test
    void updateBook_StatusNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.empty());

        bookUpdateRequestDTO = new BookUpdateRequestDTO("Updated Title", "Updated Description", 15000, false, "1");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookService.updateBook(1L, bookUpdateRequestDTO));

        assertThat(exception.getMessage()).isEqualTo("status id cannot found: 1");
    }

    @Test
    void incrementBookView_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.incrementBookView(1L);

        assertThat(book.getBookView()).isEqualTo(1);
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void incrementBookView_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookService.incrementBookView(1L));

        assertThat(exception.getMessage()).isEqualTo("book not found with id: 1");
    }


    @Test
    void updateBookAmount_Success() {
        book.setBookAmount(10); // 초기 재고 수량
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.updateBookAmount(1L, 5); // 수량 추가
        assertThat(book.getBookAmount()).isEqualTo(15);

        bookService.updateBookAmount(1L, -3); // 수량 감소
        assertThat(book.getBookAmount()).isEqualTo(12);

        verify(bookRepository, times(2)).save(book);
    }

    @Test
    void updateBookAmount_NotEnoughStock() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookService.updateBookAmount(1L, -10));

        assertThat(exception.getMessage()).isEqualTo("not enough bookAmount");
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(book);
    }

    @Test
    void incrementBookSellCount_Success() {
        book.setBookAmount(10); // 초기 재고 수량
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.incrementBookSellCount(1L, 3);

        assertThat(book.getBookSellCount()).isEqualTo(3);
        assertThat(book.getBookAmount()).isEqualTo(7); // 재고가 감소해야 함
        verify(bookRepository, times(2)).save(book);
    }


    @Test
    void incrementBookSellCount_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookService.incrementBookSellCount(1L, 3));

        assertThat(exception.getMessage()).isEqualTo("book not found with id: 1");
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(book);
    }

    @Test
    void deleteBook_Success() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void getBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBook(1L);

        assertThat(foundBook).isEqualTo(book);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookService.getBook(1L));

        assertThat(exception.getMessage()).isEqualTo("bookId not found: 1");
        verify(bookRepository, times(1)).findById(1L);
    }

}
