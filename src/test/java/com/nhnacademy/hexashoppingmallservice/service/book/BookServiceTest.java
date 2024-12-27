package com.nhnacademy.hexashoppingmallservice.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookIsbnAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.book.PublisherNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

        assertThrows(PublisherNotFoundException.class, () ->
                bookService.createBook(bookRequestDTO));
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
        assertThrows(BookNotFoundException.class, () ->
                bookService.updateBook(1L, bookUpdateRequestDTO));
    }


    @Test
    void updateBook_StatusNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookStatusRepository.findById(1L)).thenReturn(Optional.empty());

        bookUpdateRequestDTO = new BookUpdateRequestDTO("Updated Title", "Updated Description", 15000, false, "1");
        assertThrows(BookStatusNotFoundException.class, () ->
                bookService.updateBook(1L, bookUpdateRequestDTO));
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

        assertThrows(BookNotFoundException.class, () ->
                bookService.incrementBookView(1L));
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

    @Test
    void createBook_shouldThrowPublisherNotFoundException_whenPublisherDoesNotExist() {
        // Given
        BookRequestDTO requestDTO = new BookRequestDTO(
                "The Great Gatsby",               // bookTitle
                "A novel written by American author F. Scott Fitzgerald.", // bookDescription
                LocalDate.of(1925, 4, 10),         // bookPubDate
                9780743273565L,                    // bookIsbn
                20000,                             // bookOriginPrice
                15000,                             // bookPrice
                true,                              // bookWrappable
                "123456789",                    // publisherId
                "status001"                        // bookStatusId
        );

        when(publisherRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(PublisherNotFoundException.class, () -> bookService.createBook(requestDTO));
        verify(publisherRepository).existsById(anyLong());
        verifyNoInteractions(bookRepository, bookStatusRepository);
    }

    @Test
    void createBook_shouldThrowBookStatusNotFoundException_whenBookStatusDoesNotExist() {
        // Given
        BookRequestDTO requestDTO = new BookRequestDTO(
                "The Great Gatsby",               // bookTitle
                "A novel written by American author F. Scott Fitzgerald.", // bookDescription
                LocalDate.of(1925, 4, 10),         // bookPubDate
                9780743273565L,                    // bookIsbn
                20000,                             // bookOriginPrice
                15000,                             // bookPrice
                true,                              // bookWrappable
                "123456789",                    // publisherId
                "00123"                        // bookStatusId
        );

        when(publisherRepository.existsById(anyLong())).thenReturn(true);
        when(publisherRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Publisher()));
        when(bookStatusRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(BookStatusNotFoundException.class, () -> bookService.createBook(requestDTO));
        verify(bookStatusRepository).existsById(anyLong());
    }

    @Test
    void createBook_shouldThrowBookIsbnAlreadyExistException_whenIsbnAlreadyExists() {
        // Given
        BookRequestDTO requestDTO = new BookRequestDTO(
                "The Great Gatsby",               // bookTitle
                "A novel written by American author F. Scott Fitzgerald.", // bookDescription
                LocalDate.of(1925, 4, 10),         // bookPubDate
                9780743273565L,                    // bookIsbn
                20000,                             // bookOriginPrice
                15000,                             // bookPrice
                true,                              // bookWrappable
                "123456789",                    // publisherId
                "123456"                        // bookStatusId
        );

        when(publisherRepository.existsById(anyLong())).thenReturn(true);
        when(publisherRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Publisher()));
        when(bookStatusRepository.existsById(anyLong())).thenReturn(true);
        when(bookStatusRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new BookStatus()));
        when(bookRepository.existsByBookIsbn(9780743273565L)).thenReturn(true);

        // When & Then
        assertThrows(BookIsbnAlreadyExistException.class, () -> bookService.createBook(requestDTO));
        verify(bookRepository).existsByBookIsbn(9780743273565L);
    }

    @Test
    void createBook_shouldCreateBook_whenAllConditionsAreMet() {
        // Given
        BookRequestDTO requestDTO = new BookRequestDTO(
                "The Great Gatsby",               // bookTitle
                "A novel written by American author F. Scott Fitzgerald.", // bookDescription
                LocalDate.of(1925, 4, 10),         // bookPubDate
                9780743273565L,                    // bookIsbn
                20000,                             // bookOriginPrice
                15000,                             // bookPrice
                true,                              // bookWrappable
                "123456789",                    // publisherId
                "123456"                        // bookStatusId
        );

        Publisher publisher = new Publisher();
        BookStatus bookStatus = new BookStatus();
        Book book = new Book();

        when(publisherRepository.existsById(anyLong())).thenReturn(true);
        when(publisherRepository.findById(anyLong())).thenReturn(java.util.Optional.of(publisher));
        when(bookStatusRepository.existsById(anyLong())).thenReturn(true);
        when(bookStatusRepository.findById(anyLong())).thenReturn(java.util.Optional.of(bookStatus));
        when(bookRepository.existsByBookIsbn(9780743273565L)).thenReturn(false);
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);

        // When
        Book result = bookService.createBook(requestDTO);

        // Then
        assertThat(result).isNotNull();
        verify(publisherRepository).existsById(anyLong());
        verify(bookStatusRepository).existsById(anyLong());
        verify(bookRepository).save(Mockito.any(Book.class));
    }

    @Test
    void getBooks_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooks(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBooks_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooks(pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBooksByBookView_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findByOrderByBookViewDesc(pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByBookView(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findByOrderByBookViewDesc(pageable);
    }

    @Test
    void getBooksByBookView_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findByOrderByBookViewDesc(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByBookView(pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findByOrderByBookViewDesc(pageable);
    }

    @Test
    void getBooksByBookSellCount_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findByOrderByBookSellCountDesc(pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByBookSellCount(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findByOrderByBookSellCountDesc(pageable);
    }

    @Test
    void getBooksByBookSellCount_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findByOrderByBookSellCountDesc(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByBookSellCount(pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findByOrderByBookSellCountDesc(pageable);
    }

    @Test
    void getBooksByCategory_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Long> categoryIds = List.of(1L, 2L);
        List<Book> books = List.of(book);
        when(bookRepository.findBooksByCategoryIds(categoryIds, pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByCategory(categoryIds, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findBooksByCategoryIds(categoryIds, pageable);
    }

    @Test
    void getBooksByCategory_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        List<Long> categoryIds = List.of(1L, 2L);
        when(bookRepository.findBooksByCategoryIds(categoryIds, pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByCategory(categoryIds, pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findBooksByCategoryIds(categoryIds, pageable);
    }

    @Test
    void getBooksByLikeCount_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findBooksOrderByLikeCountDesc(pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByLikeCount(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findBooksOrderByLikeCountDesc(pageable);
    }

    @Test
    void getBooksByLikeCount_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findBooksOrderByLikeCountDesc(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByLikeCount(pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findBooksOrderByLikeCountDesc(pageable);
    }

    @Test
    void getBooksByBookTitle_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findByBookTitleContaining("Book Title", pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByBookTitle("Book Title", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findByBookTitleContaining("Book Title", pageable);
    }

    @Test
    void getBooksByBookTitle_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findByBookTitleContaining("Book Title", pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByBookTitle("Book Title", pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findByBookTitleContaining("Book Title", pageable);
    }

    @Test
    void getBooksByAuthorName_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findBooksByAuthorNameLike("Author Name", pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByAuthorName("Author Name", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findBooksByAuthorNameLike("Author Name", pageable);
    }

    @Test
    void getBooksByAuthorName_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findBooksByAuthorNameLike("Author Name", pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByAuthorName("Author Name", pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findBooksByAuthorNameLike("Author Name", pageable);
    }

    @Test
    void getBooksByTag_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findBooksByTagName("Fiction", pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByTag("Fiction", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findBooksByTagName("Fiction", pageable);
    }

    @Test
    void getBooksByTag_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findBooksByTagName("NonExistingTag", pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByTag("NonExistingTag", pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findBooksByTagName("NonExistingTag", pageable);
    }

    @Test
    void getBooksByBookPubDate_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findAllByOrderByBookPubDateDesc(pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByBookPubDate(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findAllByOrderByBookPubDateDesc(pageable);
    }

    @Test
    void getBooksByBookPubDate_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findAllByOrderByBookPubDateDesc(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByBookPubDate(pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findAllByOrderByBookPubDateDesc(pageable);
    }

    @Test
    void incrementBookAmount_Success() {
        book.setBookAmount(10);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.incrementBookAmount(1L, 5);

        assertThat(book.getBookAmount()).isEqualTo(15);
    }


    @Test
    void incrementBookAmount_NegativeResultingStock() {
        book.setBookAmount(5);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(BookNotFoundException.class, () ->
                bookService.incrementBookAmount(1L, -10));

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(book);
    }

    @Test
    void getAuthors_Success() {
        Long bookId = 1L;
        List<Author> authors = List.of(Author.of("Author Name"));
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookRepository.findAuthorsByBookId(bookId)).thenReturn(authors);

        List<Author> result = bookService.getAuthors(bookId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthorName()).isEqualTo("Author Name");
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).findAuthorsByBookId(bookId);
    }

    @Test
    void getAuthors_BookNotFound() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.getAuthors(bookId));

        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, never()).findAuthorsByBookId(bookId);
    }

    @Test
    void getBooksByPublisherName_Success() {
        Pageable pageable = mock(Pageable.class);
        List<Book> books = List.of(book);
        when(bookRepository.findBooksByPublisherName("PublisherName", pageable)).thenReturn(new PageImpl<>(books));

        List<Book> result = bookService.getBooksByPublisherName("PublisherName", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookTitle()).isEqualTo("Book Title");

        verify(bookRepository, times(1)).findBooksByPublisherName("PublisherName", pageable);
    }

    @Test
    void getBooksByPublisherName_EmptyResult() {
        Pageable pageable = mock(Pageable.class);
        when(bookRepository.findBooksByPublisherName("PublisherName", pageable)).thenReturn(new PageImpl<>(List.of()));

        List<Book> result = bookService.getBooksByPublisherName("PublisherName", pageable);

        assertThat(result).isEmpty();

        verify(bookRepository, times(1)).findBooksByPublisherName("PublisherName", pageable);
    }


}
