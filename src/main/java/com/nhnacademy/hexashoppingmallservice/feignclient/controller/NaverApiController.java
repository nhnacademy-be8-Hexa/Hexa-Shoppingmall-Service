package com.nhnacademy.hexashoppingmallservice.feignclient.controller;

import com.nhnacademy.hexashoppingmallservice.feignclient.domain.naver.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.service.NaverApiService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NaverApiController {

    @Autowired
    private NaverApiService naverApiService;

    @GetMapping("/api/naverApi")
    private ResponseEntity<List<Book>> getBooks(@RequestParam String query) {
        return ResponseEntity.ok(naverApiService.searchBooks(query));
    }
}
