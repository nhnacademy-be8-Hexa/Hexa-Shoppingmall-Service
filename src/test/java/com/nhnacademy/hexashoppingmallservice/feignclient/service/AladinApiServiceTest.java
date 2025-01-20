package com.nhnacademy.hexashoppingmallservice.feignclient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookIsbnAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.feignclient.AladinApi;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AladinApiServiceTest {

    @Mock
    private AladinApi aladinApi;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookStatusRepository bookStatusRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @InjectMocks
    private AladinApiService aladinApiService;

    private AladinBookRequestDTO aladinBookRequestDTO;
    private BookStatus bookStatus;
    private Publisher publisher;
    private Book book;
    private Author author1;
    private Author author2;

    @BeforeEach
    public void setUp() {

        aladinBookRequestDTO = new AladinBookRequestDTO(
                "Test Book",
                Arrays.asList("Author1", "Author2"),
                1500,
                2000,
                "Test Publisher",
                "1",
                LocalDate.parse("2022-01-01"),
                1234567890123L,
                "Test Description",
                true,
                10
        );

        bookStatus = BookStatus.of("Available");
        publisher = Publisher.of("Test Publisher");

        book = Book.of(
                "Test Book",
                "Test Description",
                LocalDate.parse("2022-01-01"),
                1234567890123L,
                2000,
                1500,
                publisher,
                bookStatus
        );

        author1 = Author.of("Author1");
        author2 = Author.of("Author2");
    }

    @Test
    public void testSearchBooks() {
        String mockResponse =
                "{\"item\": " +
                        "[{\"title\": " + "\"Test Book\", " +
                        "\"author\": \"Author1, Author2\", " +
                        "\"isbn13\": \"1234567890123\", " +
                        "\"priceSales\": \"1500\", " +
                        "\"priceStandard\": \"2000\", " +
                        "\"publisher\": \"Test Publisher\", " +
                        "\"pubDate\": \"2022-01-01\", " +
                        "\"description\": \"Test Description\", " +
                        "\"salesPoint\": \"100\", " +
                        "\"cover\": \"coverUrl\"}]}";

        ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

        when(aladinApi.searchBooks(anyString(), anyString(), anyString(), anyString())).thenReturn(responseEntity);

        List<AladinBookDTO> result = aladinApiService.searchBooks("Sample Book");

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        assertTrue(result.get(0).getAuthors().contains("Author1"));
        assertEquals(1234567890123L, result.get(0).getIsbn13());
        assertEquals("Test Publisher", result.get(0).getPublisher());
        assertEquals(1500, result.get(0).getPriceSales());
    }

    @Test
    public void testCreateAladinBook_WhenBookIsbnAlreadyExists() {
        when(bookStatusRepository.existsById(anyLong())).thenReturn(true);
        when(bookRepository.existsByBookIsbn(anyLong())).thenReturn(true);
        when(bookStatusRepository.findById(anyLong())).thenReturn(Optional.of(bookStatus));

        BookIsbnAlreadyExistException exception = assertThrows(
                BookIsbnAlreadyExistException.class,
                () -> aladinApiService.createAladinBook(aladinBookRequestDTO),
                "Expected BookIsbnAlreadyExistException to be thrown"
        );

        assertTrue(exception.getMessage().contains("isbn - 1234567890123 already exist"));

        verify(bookRepository, times(1)).existsByBookIsbn(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    public void testCreateAladinBook_WhenBookStatusNotFound() {
        when(bookStatusRepository.existsById(anyLong())).thenReturn(false);

        BookStatusNotFoundException thrown = assertThrows(BookStatusNotFoundException.class,
                () -> aladinApiService.createAladinBook(aladinBookRequestDTO));

        assertTrue(thrown.getMessage().contains("status id - 1 is not found"));

        verify(bookStatusRepository, times(1)).existsById(anyLong());
        verifyNoMoreInteractions(bookStatusRepository);
    }

    @Test
    public void testCreateAladinBook_Success() {

        when(publisherRepository.findByPublisherName(anyString())).thenReturn(null);
        when(publisherRepository.save(any())).thenReturn(publisher);

        when(bookStatusRepository.existsById(anyLong())).thenReturn(true);
        when(bookStatusRepository.findById(anyLong())).thenReturn(Optional.of(bookStatus));

        when(bookRepository.existsByBookIsbn(anyLong())).thenReturn(false);
        when(bookRepository.save(any())).thenReturn(book);

        when(authorRepository.save(any()))
                .thenReturn(author1)
                .thenReturn(author2);

        when(bookAuthorRepository.save(any())).thenReturn(null);
        com.nhnacademy.hexashoppingmallservice.entity.book.Book result =
                aladinApiService.createAladinBook(aladinBookRequestDTO);

        assertNotNull(result);
        assertEquals(book, result);

        verify(bookRepository, times(1)).existsByBookIsbn(anyLong());
        verify(bookRepository, times(1)).save(any());
        verify(bookStatusRepository, times(1)).existsById(anyLong());
        verify(bookAuthorRepository, times(2)).save(any());
    }
}
