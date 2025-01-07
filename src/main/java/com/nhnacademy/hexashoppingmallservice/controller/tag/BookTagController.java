package com.nhnacademy.hexashoppingmallservice.controller.tag;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.service.tag.BookTagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookTagController {
    private final BookTagService bookTagService;

    @PostMapping("/admin/books/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> addBookTag(@PathVariable Long bookId, @PathVariable Long tagId) {
        bookTagService.create(bookId, tagId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/books/{bookId}/tags")
    public ResponseEntity<List<Tag>> getTagsByBook(@PathVariable Long bookId) {
        List<Tag> tagList = bookTagService.getTagsByBookId(bookId);
        return ResponseEntity.ok(tagList);
    }

    @GetMapping("/tags/{tagId}/books")
    public ResponseEntity<List<Book>> getBooksByTag(@PathVariable Long tagId, Pageable pageable) {
        List<Book> bookList = bookTagService.getBooksByTagId(tagId, pageable);
        return ResponseEntity.ok(bookList);
    }

    @DeleteMapping("/admin/books/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> deleteBookTag(@PathVariable Long bookId, @PathVariable Long tagId) {
        bookTagService.delete(bookId, tagId);
        return ResponseEntity.ok().build();
    }

}
