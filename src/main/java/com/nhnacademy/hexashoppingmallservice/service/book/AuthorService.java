package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookAuthor;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final BookAuthorRepository bookAuthorRepository;

    @Transactional
    public Author createAuthor(String authorName, Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("book not found with id - %d".formatted(bookId));
        }
        Book book = bookRepository.findById(bookId).get();
        Author author = Author.of(authorName);
        if(Objects.isNull(authorRepository.findByAuthorName(authorName))) {
            author = authorRepository.save(author);
        } else {
            author = authorRepository.findByAuthorName(authorName);
        }

        BookAuthor bookAuthor = BookAuthor.of(book, author);
        bookAuthorRepository.save(bookAuthor);
        return author;
    }

    // 도서가 삭제되지 않으므로(도서 상태만 변경됨) 저자 역시 삭제 기능 구현하지 아니함
}
