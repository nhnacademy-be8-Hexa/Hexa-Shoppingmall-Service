package com.nhnacademy.hexashoppingmallservice.feignclient.controller;

import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.service.AladinApiService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AladinApiController {

    @Autowired
    private AladinApiService aladinApiService;

    @GetMapping("/api/aladinApi")
    private List<Book> createBooks(@RequestParam String query) {
        return aladinApiService.searchBooks(query);
    }

    @PostMapping("/api/aladinApi")
    private List<Book> getBooks(@RequestParam String query,
                                @RequestParam Long bookStatusId,
                                @RequestParam Long publisherId) {
        return aladinApiService.createBooks(query, bookStatusId, publisherId);
    }

}
