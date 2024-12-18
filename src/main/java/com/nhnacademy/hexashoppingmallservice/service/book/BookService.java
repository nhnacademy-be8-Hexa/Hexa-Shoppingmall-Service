package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BookStatusRepository bookStatusRepository;

    @Transactional
    public Book createBook(BookRequestDTO bookRequestDTO) {
        if (!publisherRepository.existsById(Long.parseLong(bookRequestDTO.getPublisherId()))) {
            throw new RuntimeException("publisher id is not found " + bookRequestDTO.getPublisherId());
        }

        Publisher publisher = publisherRepository.findById(Long.parseLong(bookRequestDTO.getPublisherId())).orElseThrow();

        if (!bookStatusRepository.existsById(Long.parseLong(bookRequestDTO.getBookStatusId()))) {
            throw new RuntimeException("status id is not found " + bookRequestDTO.getBookStatusId());
        }

        BookStatus bookStatus = bookStatusRepository.findById(Long.parseLong(bookRequestDTO.getBookStatusId())).orElseThrow();

        // isbn 중복 체크
        if (bookRepository.existsById(bookRequestDTO.getBookIsbn())) {
            throw new RuntimeException("isbn already exist " + bookRequestDTO.getBookIsbn());
        }

        Book book = Book.of(
                bookRequestDTO.getBookTitle(),
                bookRequestDTO.getBookDescription(),
                bookRequestDTO.getBookPubDate(),
                bookRequestDTO.getBookIsbn(),
                bookRequestDTO.getBookOriginPrice(),
                bookRequestDTO.getBookPrice(),
                publisher,
                bookStatus
        );

        return bookRepository.save(book);
    }

//    @Transactional
//    public List<Book> g

    // 도서 목록 - 조회수 (내림차순)
    // 도서 목록 - 베스트셀러 (내림차순)
    // 도서 목록 - 카테고리 별
    //좋아요 (내림차순)
    // 도서 목록 - 출판사
    // 도서 목록 - 도서명
    // 도서 목록 저자 이름

}
