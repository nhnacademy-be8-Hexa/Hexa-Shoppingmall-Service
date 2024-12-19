package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.service.book.BookStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookStatusController {
    private final BookStatusService bookStatusService;

    @GetMapping("api/bookStatuses")
    public List<BookStatus> getBookStatus(){
        return bookStatusService.getAllBookStatus();
    }

    @PostMapping("api/bookStatuses")
    public ResponseEntity<BookStatus> createBookStatus(@RequestBody @Valid BookStatus bookStatus){
        return ResponseEntity.ok().body(bookStatusService.createBookStatus(bookStatus));
    }

    @GetMapping("api/bookStatuses/{bookStatusId}")
    public BookStatus getBookStatus(@PathVariable Long bookStatusId){
        return bookStatusService.getBookStatus(bookStatusId);
    }
}
