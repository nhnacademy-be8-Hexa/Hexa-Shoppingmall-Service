package com.nhnacademy.hexashoppingmallservice.feignclient.controller;

import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.service.AladinApiService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AladinApiController {

    @Autowired
    private AladinApiService aladinApiService;

    @GetMapping("/api/aladinApi")
    private List<AladinBookDTO> searchBooks(@RequestParam(required = false) String query) {
        return aladinApiService.searchBooks(query);
    }

//    @PostMapping("/api/aladinApi")
//    private List<Book> createBooks(@RequestParam String query,
//                                   @RequestParam Long bookStatusId) {
//        return aladinApiService.createBooks(query, bookStatusId);
//    }

}
