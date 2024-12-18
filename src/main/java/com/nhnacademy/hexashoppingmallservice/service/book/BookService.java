package com.nhnacademy.hexashoppingmallservice.service.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            throw new RuntimeException("publisher id is not found " + bookRequestDTO.getPublisherId());
        }

        Publisher publisher = publisherRepository.findById(Long.parseLong(bookRequestDTO.getPublisherId())).orElseThrow();

        if (!bookStatusRepository.existsById(Long.parseLong(bookRequestDTO.getBookStatusId()))) {
            throw new RuntimeException("status id is not found " + bookRequestDTO.getBookStatusId());
        }

        BookStatus bookStatus = bookStatusRepository.findById(Long.parseLong(bookRequestDTO.getBookStatusId())).orElseThrow();

        // isbn 중복 체크
        if (bookRepository.existsByBookIsbn(bookRequestDTO.getBookIsbn())) {
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
        return bookRepository.findByOrderByBookSellCount(pageable).getContent();
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
        return bookRepository.findByBookTitleLike(bookTitle, pageable).getContent();
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
                () -> new RuntimeException("bookId cannot found: " + bookId)
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
                    .orElseThrow(() -> new RuntimeException("status id cannot found: " + statusId));
            book.setBookStatus(bookStatus);
        }

        return book;
    }

    // 조회수
    @Transactional
    public void incrementBookView(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("book not found with id: " + bookId));

        book.setBookView(book.getBookView() + 1);

        bookRepository.save(book);
    }

    @Transactional
    public void updateBookAmount(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new RuntimeException("book not found with id: "+bookId));

        int updateAmount = book.getBookAmount() + quantity;
        if(updateAmount < 0){
            throw new RuntimeException("not enough bookAmount");
        }

        book.setBookAmount(updateAmount);

        bookRepository.save(book);
    }

    // 판매수
    @Transactional
    public void incrementBookSellCount(Long bookId, int quantity){
        updateBookAmount(bookId,quantity);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new RuntimeException("book not found with id: "+bookId));

        book.setBookSellCount(book.getBookSellCount() + quantity);

        bookRepository.save(book);
    }

    //delete
    @Transactional
    public void deleteBook(Long bookId){
        bookRepository.deleteById(bookId);
    }

}
