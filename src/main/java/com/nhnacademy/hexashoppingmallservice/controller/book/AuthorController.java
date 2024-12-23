package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.AuthorRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.service.book.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/authors")
public class AuthorController {
    private final AuthorService authorService;

    @PostMapping
    public Author createAuthor(@RequestBody @Valid AuthorRequestDTO authorRequestDTO) {
        return authorService.createAuthor(authorRequestDTO.getAuthorName(), authorRequestDTO.getBookId());
    }
}
