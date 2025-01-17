package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.service.book.BookService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final JwtUtils jwtUtils;

    // 통합된 도서 목록 조회
    @GetMapping
    public List<Book> getBooks(
            Pageable pageable,
            //도서 제목으로 검색
            @RequestParam(required = false) String search,
            //카테고리(아이디)로 검색
            @RequestParam(required = false) List<Long> categoryIds,
            //출판사명으로 검색
            @RequestParam(required = false) String publisherName,
            //작가명으로 검색
            @RequestParam(required = false) String authorName,
            //태그명 검색은 tagAPI에서 구현됨
//            @RequestParam(required = false) String tagName,
            //조회수에 의한 정렬
            @RequestParam(required = false) Boolean sortByView,
            //판매수에 의한 정렬
            @RequestParam(required = false) Boolean sortBySellCount,
            //좋아요수에 의한 정렬
            @RequestParam(required = false) Boolean sortByLikeCount,
            //출간일 최신순으로 정렬
            @RequestParam(required = false) Boolean latest,
            // 도서명 내림차순
            @RequestParam(required = false) Boolean sortByBookTitleDesc,
            // 도서명 오름차순
            @RequestParam(required = false) Boolean sortByBookTitleAsc,
            // 리뷰순
            @RequestParam(required = false) Boolean sortByReviews
    ) {
        if (search != null && !search.isEmpty()) {
            return bookService.getBooksByBookTitle(search, pageable);
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return bookService.getBooksByCategory(categoryIds, pageable);
        }
        if (publisherName != null && !publisherName.isEmpty()) {
            return bookService.getBooksByPublisherName(publisherName, pageable);
        }
        if (authorName != null && !authorName.isEmpty()) {
            return bookService.getBooksByAuthorName(authorName, pageable);
        }
//        if(tagName != null && !tagName.isEmpty()){
//            return bookService.getBooksByTag(tagName, pageable);
//        }
        //가장 많이 열람한 횟수로 정렬
        if (sortByView != null && sortByView) {
            return bookService.getBooksByBookView(pageable);
        }
        if (sortBySellCount != null && sortBySellCount) {
            return bookService.getBooksByBookSellCount(pageable);
        }
        if (sortByLikeCount != null && sortByLikeCount) {
            return bookService.getBooksByLikeCount(pageable);
        }
        if (latest != null && latest) {
            return bookService.getBooksByBookPubDate(pageable);
        }
        if (sortByBookTitleDesc != null && sortByBookTitleDesc) {
            return bookService.getBooksByNameDesc(pageable);
        }
        if (sortByBookTitleAsc != null && sortByBookTitleAsc) {
            return bookService.getBooksByNameAsc(pageable);
        }
        if (sortByReviews != null && sortByReviews) {
            return bookService.getBooksByIsbnAsc(pageable);
        }
        return bookService.getBooks(pageable);

    }

    // 도서 아이디리스트를 이용한 도서 목록 조회
    @GetMapping("/ids")
    public List<Book> getBooksByIds(
            @RequestParam List<Long> bookIds
    ) {
        return bookService.getBooksByIds(bookIds);
    }

    // 도서 생성
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody @Valid BookRequestDTO bookRequestDTO,
                                           HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.status(201).body(bookService.createBook(bookRequestDTO));
    }

    // 도서 아이디로 조회
    @GetMapping("/{bookId}")
    public Book getBook(@PathVariable Long bookId) {
        return bookService.getBook(bookId);
    }

    // 도서 수정
    @PutMapping("/{bookId}")
    public Book updateBook(@PathVariable Long bookId, @RequestBody @Valid BookUpdateRequestDTO bookRequestDTO,
                           HttpServletRequest request) {
        return bookService.updateBook(bookId, bookRequestDTO);
    }

    // 도서 조회수 증가
    @PutMapping("/{bookId}/view")
    public ResponseEntity<Void> incrementBookView(@PathVariable Long bookId) {
        bookService.incrementBookView(bookId);
        return ResponseEntity.noContent().build();
    }

    // 도서 판매량 증가
    @PutMapping("/{bookId}/sell-count")
    public ResponseEntity<Void> incrementBookSellCount(@PathVariable Long bookId, @RequestParam int quantity) {
        bookService.incrementBookSellCount(bookId, quantity);
        return ResponseEntity.noContent().build();
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    // 도서 작가 목록 조회
    @GetMapping("/{bookId}/authors")
    public List<Author> getAuthors(@PathVariable Long bookId) {
        return bookService.getAuthors(bookId);
    }

    // 책 수량 증가
    @GetMapping("/{bookId}/amount-increase")
    public ResponseEntity<Void> incrementBookAmountIncrease(@PathVariable Long bookId, @RequestParam int quantity,
                                                            HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        bookService.incrementBookAmount(bookId, quantity);
        return ResponseEntity.noContent().build();
    }

    // 도서 총계 조회(페이징용)
    @GetMapping("total")
    public ResponseEntity<Long> getTotalBooks(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "publisherName", required = false) String publisherName,
            @RequestParam(value = "authorName", required = false) String authorName
    ) {
        return ResponseEntity.ok(bookService.getTotal(search, categoryIds, publisherName, authorName));
    }
}
