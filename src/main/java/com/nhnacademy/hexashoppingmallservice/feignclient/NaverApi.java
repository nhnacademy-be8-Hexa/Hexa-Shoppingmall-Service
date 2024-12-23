package com.nhnacademy.hexashoppingmallservice.feignclient;

import com.nhnacademy.hexashoppingmallservice.feignclient.domain.naver.ListBook;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverApi", url = "https://openapi.naver.com/v2/search/book.json")
public interface NaverApi {

    @GetMapping
    ResponseEntity<ListBook> searchBooks(
            @RequestHeader("X-Naver-Client-Id") String clientId,
            @RequestHeader("X-Naver-Client-Secret") String clientSecret,
            @RequestParam("query") String query
    );

}
