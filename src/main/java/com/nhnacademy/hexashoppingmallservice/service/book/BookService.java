package com.nhnacademy.hexashoppingmallservice.service.book;

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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BookStatusRepository bookStatusRepository;

    @Transactional
    public Book createBook(BookRequestDTO bookRequestDTO) {
        if (!publisherRepository.existsById(Long.parseLong(bookRequestDTO.getPublisherId()))) {
            throw new PublisherNotFoundException(
                    "publisher id - %s is not found".formatted(bookRequestDTO.getPublisherId()));
        }

        Publisher publisher =
                publisherRepository.findById(Long.parseLong(bookRequestDTO.getPublisherId())).orElseThrow();

        if (!bookStatusRepository.existsById(Long.parseLong(bookRequestDTO.getBookStatusId()))) {
            throw new BookStatusNotFoundException(
                    "status id - %s is not found".formatted(bookRequestDTO.getBookStatusId()));
        }

        BookStatus bookStatus =
                bookStatusRepository.findById(Long.parseLong(bookRequestDTO.getBookStatusId())).orElseThrow();

        // isbn 중복 체크
        if (bookRepository.existsByBookIsbn(bookRequestDTO.getBookIsbn())) {
            throw new BookIsbnAlreadyExistException("isbn - %d already exist ".formatted(bookRequestDTO.getBookIsbn()));
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

    // 도서 전체 조회
    public List<Book> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).getContent();
    }

    // 도서 목록 - 조회수 (내림차순)
    public List<Book> getBooksByBookView(Pageable pageable) {
        return bookRepository.findByOrderByBookViewDesc(pageable).getContent();
    }

    // 도서 목록 - 베스트셀러 (내림차순)
    public List<Book> getBooksByBookSellCount(Pageable pageable) {
        return bookRepository.findByOrderByBookSellCountDesc(pageable).getContent();
    }

    // 도서 목록 - 카테고리 별
    public List<Book> getBooksByCategory(List<Long> categoryIds, Pageable pageable) {
        // 카테고리 아이디들이 비어있으면 빈 리스트 반환하게 하는 로직 추가하면 좋을듯
        return bookRepository.findBooksByCategoryIds(categoryIds, pageable).getContent();
    }

    //좋아요 (내림차순)
    public List<Book> getBooksByLikeCount(Pageable pageable) {
        return bookRepository.findBooksOrderByLikeCountDesc(pageable).getContent();
    }

    // 도서 목록 - 출판사
    public List<Book> getBooksByPublisherName(String publisherName, Pageable pageable) {
        return bookRepository.findBooksByPublisherName(publisherName, pageable).getContent();
    }

    // 도서 목록 - 도서명
    public List<Book> getBooksByBookTitle(String bookTitle, Pageable pageable) {
        return bookRepository.findByBookTitleContaining(bookTitle, pageable).getContent();
    }

    // 도서 목록 저자 이름
    public List<Book> getBooksByAuthorName(String authorName, Pageable pageable) {
        return bookRepository.findBooksByAuthorNameLike(authorName, pageable).getContent();
    }

    // 도서 목록 - 태그
    public List<Book> getBooksByTag(String tagName, Pageable pageable) {
        return bookRepository.findBooksByTagName(tagName, pageable).getContent();
    }

    // 도서 목록 - 최신순(출간일 기준)
    public List<Book> getBooksByBookPubDate(Pageable pageable) {
        return bookRepository.findAllByOrderByBookPubDateDesc(pageable).getContent();
    }

    // 도서 수정
    @Transactional
    public Book updateBook(Long bookId, BookUpdateRequestDTO bookRequestDTO) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException("bookId %d cannot found".formatted(bookId))
        );

        // 제목
        if (bookRequestDTO.getBookTitle() != null) {
            book.setBookTitle(bookRequestDTO.getBookTitle());
        }

        // 설명
        if (bookRequestDTO.getBookDescription() != null) {
            book.setBookDescription(bookRequestDTO.getBookDescription());
        }

        // 판매가
        if (bookRequestDTO.getBookPrice() != 0) {
            book.setBookPrice(bookRequestDTO.getBookPrice());
        }

        // 포장 여부
        if (bookRequestDTO.getBookWrappable() != null) {
            book.setBookWrappable(bookRequestDTO.getBookWrappable());
        }

        // 상태
        if (bookRequestDTO.getStatusId() != null) {
            Long statusId = Long.parseLong(bookRequestDTO.getStatusId());
            BookStatus bookStatus = bookStatusRepository.findById(statusId)
                    .orElseThrow(
                            () -> new BookStatusNotFoundException("status id - %d cannot found: ".formatted(statusId)));
            book.setBookStatus(bookStatus);
        }

        return bookRepository.save(book);
    }


    // 조회수
    @Transactional
    public void incrementBookView(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("book not found with id - %d".formatted(bookId)));

        book.setBookView(book.getBookView() + 1);

        bookRepository.save(book);
    }


    public void updateBookAmount(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("book not found with id: " + bookId));

        int updateAmount = book.getBookAmount() + quantity;
        if (updateAmount < 0) {
            throw new BookNotFoundException("not enough bookAmount");
        }
        
        book.setBookAmount(updateAmount);
        bookRepository.save(book);
    }

    @Transactional
    public void incrementBookSellCount(Long bookId, int quantity) {
        // 판매량 증가와 동시에 재고 감소 처리
        updateBookAmount(bookId, -quantity);

        Book book = bookRepository.findById(bookId).get();

        book.setBookSellCount(book.getBookSellCount() + quantity);
        bookRepository.save(book);
    }

    @Transactional
    // 재고 증가 처리
    public void incrementBookAmount(Long bookId, int quantity) {
        updateBookAmount(bookId, quantity);
    }


    //delete
    @Transactional
    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }

    // 도서 아이디로 조회
    public Book getBook(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException("bookId not found: " + bookId)
        );
    }

    // 도서 아이디 리스트로 조회
    public List<Book> getBooksByIds(List<Long> bookIds) {
        return bookRepository.findAllById(bookIds);
    }

    // 도서 작가 목록 조회
    public List<Author> getAuthors(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("book not found with id: " + bookId);
        }

        return bookRepository.findAuthorsByBookId(bookId);
    }

    // 도서 총계 조회 (페이징용)
    @Transactional(readOnly = true)
    public Long getTotal(String search, List<Long> categoryIds, String publisherName, String authorName) {
        if (search != null && !search.isEmpty()) {
            return bookRepository.countByBookTitleContaining(search);
        } else if (categoryIds != null && !categoryIds.isEmpty()) {
            return bookRepository.countByCategoryIds(categoryIds);
        } else if (publisherName != null && !publisherName.isEmpty()) {
            return bookRepository.countByPublisherName(publisherName);
        } else if (authorName != null && !authorName.isEmpty()) {
            return bookRepository.countByAuthorName(authorName);
        } else {
            return bookRepository.count();
        }
    }
}
