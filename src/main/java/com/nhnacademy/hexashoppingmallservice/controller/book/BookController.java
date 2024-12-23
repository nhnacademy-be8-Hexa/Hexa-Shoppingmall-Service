package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

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
            @RequestParam(required = false) Boolean latest
    ){
        if(search != null && !search.isEmpty()){
            return bookService.getBooksByBookTitle(search, pageable);
        }
        if(categoryIds != null && !categoryIds.isEmpty()){
            return bookService.getBooksByCategory(categoryIds, pageable);
        }
        if(publisherName != null && !publisherName.isEmpty()){
            return bookService.getBooksByPublisherName(publisherName, pageable);
        }
        if(authorName != null && !authorName.isEmpty()){
            return bookService.getBooksByAuthorName(authorName, pageable);
        }
//        if(tagName != null && !tagName.isEmpty()){
//            return bookService.getBooksByTag(tagName, pageable);
//        }
        //가장 많이 열람한 횟수로 정렬
        if(sortByView != null && sortByView){
            return bookService.getBooksByBookView(pageable);
        }
        if(sortBySellCount != null && sortBySellCount){
            return bookService.getBooksByBookSellCount(pageable);
        }
        if(sortByLikeCount != null && sortByLikeCount){
            return bookService.getBooksByLikeCount(pageable);
        }
        if(latest != null && latest){
            return bookService.getBooksByBookPubDate(pageable);
        }
        return bookService.getBooks(pageable);
    }

    // 도서 생성
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody @Valid BookRequestDTO bookRequestDTO){
        return ResponseEntity.status(201).body(bookService.createBook(bookRequestDTO));
    }

    // 도서 아이디로 조회
    @GetMapping("/{bookId}")
    public Book getBook(@PathVariable Long bookId){
        return bookService.getBook(bookId);
    }

    // 도서 수정
    @PutMapping("/{bookId}")
    public Book updateBook(@PathVariable Long bookId, @RequestBody @Valid BookUpdateRequestDTO bookRequestDTO){
        return bookService.updateBook(bookId, bookRequestDTO);
    }

    // 도서 조회수 증가
    @PatchMapping("/{bookId}/view")
    public ResponseEntity<Void> incrementBookView(@PathVariable Long bookId){
        bookService.incrementBookView(bookId);
        return ResponseEntity.noContent().build();
    }

    // 도서 판매량 증가
    @PatchMapping("/{bookId}/sell-count")
    public ResponseEntity<Void> incrementBookSellCount(@PathVariable Long bookId, @RequestParam int quantity){
        bookService.incrementBookSellCount(bookId, quantity);
        return ResponseEntity.noContent().build();
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId){
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    // 도서 작가 목록 조회
    @GetMapping("/{bookId}/authors")
    public List<Author> getAuthors(@PathVariable Long bookId){
        return bookService.getAuthors(bookId);
    }

    @GetMapping("/{bookId}/amount-increase")
    public ResponseEntity<Void> incrementBookAmountIncrease(@PathVariable Long bookId, @RequestParam int quantity){
        bookService.incrementBookAmount(bookId, quantity);
        return ResponseEntity.noContent().build();
    }
}
