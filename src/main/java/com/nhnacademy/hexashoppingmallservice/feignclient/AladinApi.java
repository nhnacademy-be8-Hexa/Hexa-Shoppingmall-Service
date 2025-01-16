package com.nhnacademy.hexashoppingmallservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "aladinApi", url = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
public interface AladinApi {
    @GetMapping
    ResponseEntity<String> searchBooks(
            @RequestParam("ttbKey") String ttbKey,
            @RequestParam("query") String query,
            @RequestParam("output") String output,
            @RequestParam("version") String version
    );
}
