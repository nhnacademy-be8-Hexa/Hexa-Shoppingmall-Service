package com.nhnacademy.hexashoppingmallservice.feignclient.controller;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.service.AladinApiService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AladinApiController {

    @Autowired
    private AladinApiService aladinApiService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/api/aladinApi")
    private List<AladinBookDTO> searchAladinBooks(@RequestParam(required = false) String query,
                                                  HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return aladinApiService.searchBooks(query);
    }

    @PostMapping("/api/aladinApi")
    private ResponseEntity<Book> createAladinBook(@RequestBody AladinBookRequestDTO aladinBookRequestDTO,
                                                  HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return ResponseEntity.status(201).body(aladinApiService.createAladinBook(aladinBookRequestDTO));
    }

}
