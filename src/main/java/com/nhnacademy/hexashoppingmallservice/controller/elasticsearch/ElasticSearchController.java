package com.nhnacademy.hexashoppingmallservice.controller.elasticsearch;

import com.nhnacademy.hexashoppingmallservice.dto.book.SearchBookDTO;
import com.nhnacademy.hexashoppingmallservice.service.elasticsearch.ElasticSearchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/search")
public class ElasticSearchController {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping
    public List<SearchBookDTO> searchBooks(@RequestParam("search") String search, Pageable pageable) {
        return elasticSearchService.searchBooks(search, pageable);
    }

    @GetMapping("total")
    public ResponseEntity<Long> getTotalBooks(
            @RequestParam("search") String search) {
        return ResponseEntity.ok(elasticSearchService.getTotal(search));
    }

}
