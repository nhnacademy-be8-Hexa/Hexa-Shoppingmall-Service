package com.nhnacademy.hexashoppingmallservice.service.book;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookAuthor;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void createAuthor_BookNotFound() {
        Long bookId = 1L;
        String authorName = "Author Name";

        when(bookRepository.existsById(bookId)).thenReturn(false);

        Assertions.assertThrows(BookNotFoundException.class, () -> authorService.createAuthor(authorName, bookId));

        verify(bookRepository, times(1)).existsById(bookId);
        verifyNoInteractions(authorRepository, bookAuthorRepository);
    }
    
    @Test
    void createAuthor_Success() throws NoSuchFieldException, IllegalAccessException {
        Long bookId = 1L;
        String authorName = "Author Name";
        Book book = mock(Book.class);

        Author author = Author.of(authorName);
        Field authorIdField = author.getClass().getDeclaredField("authorId");
        authorIdField.setAccessible(true);
        authorIdField.set(author, 1L);

        BookAuthor bookAuthor = BookAuthor.of(book, author);

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(bookAuthorRepository.save(any(BookAuthor.class))).thenReturn(bookAuthor);

        Author result = authorService.createAuthor(authorName, bookId);

        verify(authorRepository, times(1)).save(any(Author.class));
        verify(bookAuthorRepository, times(1)).save(any(BookAuthor.class));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(authorName, result.getAuthorName());
    }
}
