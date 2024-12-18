package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final Integer SIZE = 10;
    private final BookService bookService;
    // 전체 조회
    // 어쨋든 뭐시기 해야하니까 다 받아서 required = false 해서 요청 온 것만 포함해서 if문 해서 로직 추가하기
    @GetMapping("/api/books/")
    public List<Book> getBooks(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String search){
        Pageable pageable = PageRequest.of(page,SIZE);
        if(search != null && !search.isEmpty()){
            return bookService.getBooksByBookTitle(search,pageable);
        }
        return bookService.getBooks(pageable);
    }
    // 생성
    @GetMapping("/api/books")
    public ResponseEntity<Book> createBook(@RequestBody @Valid BookRequestDTO bookRequestDTO){
        return ResponseEntity.status(201).body(bookService.createBook(bookRequestDTO));
    }

    // 아이디로 조회
    @GetMapping("/api/books/{bookId}")
    public Book getBook(@PathVariable Long bookId){
        return bookService.getBook(bookId);
    }
}
