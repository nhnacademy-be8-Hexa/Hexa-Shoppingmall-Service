package com.nhnacademy.hexashoppingmallservice.controller.elasticsearch;

import com.nhnacademy.hexashoppingmallservice.document.Book;
import com.nhnacademy.hexashoppingmallservice.service.elasticsearch.ElasticSearchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/books")
public class ElasticSearchController {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return elasticSearchService.saveBook(book);
    }

    @GetMapping("/search/title")
    public List<Book> searchBooksByTitle(@RequestParam("title") String title) {
        return elasticSearchService.searchBooksByTitle(title);
    }

    @GetMapping("/search/author")
    public List<Book> searchBooksByAuthor(@RequestParam("author") String author) {
        return elasticSearchService.searchBooksByAuthor(author);
    }

    @GetMapping("/search/description")
    public List<Book> searchBooksByDescription(@RequestParam("description") String description) {
        return elasticSearchService.searchBooksByDescription(description);
    }

    @GetMapping("/search/tag")
    public List<Book> searchBooksByTag(@RequestParam("tag") String tag) {
        return elasticSearchService.searchBooksByTag(tag);
    }

}
