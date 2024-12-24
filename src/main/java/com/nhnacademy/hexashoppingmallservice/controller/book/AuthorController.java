package com.nhnacademy.hexashoppingmallservice.controller.book;

import com.nhnacademy.hexashoppingmallservice.dto.book.AuthorRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.service.book.AuthorService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorService authorService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public Author createAuthor(@RequestBody @Valid AuthorRequestDTO authorRequestDTO, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return authorService.createAuthor(authorRequestDTO.getAuthorName(), authorRequestDTO.getBookId());
    }
}
