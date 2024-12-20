package com.nhnacademy.hexashoppingmallservice.controller.elasticsearch;

import com.nhnacademy.hexashoppingmallservice.document.Book;
import com.nhnacademy.hexashoppingmallservice.service.elasticsearch.ElasticSearchService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/books")
public class ElasticSearchController {

    @Autowired
    private ElasticSearchService elasticSearchService;

//    @PostMapping
//    public Book addBook(@RequestBody Book book) {
//        return elasticSearchService.saveBook(book);
//    }

    @GetMapping("/search/title")
    public List<Book> searchBooksByTitle(@RequestParam("title") String title, Pageable pageable) {
        return elasticSearchService.searchBooksByTitle(title, pageable);
    }

    @GetMapping("/search/author")
    public List<Book> searchBooksByAuthor(@RequestParam("author") String author, Pageable pageable) {
        return elasticSearchService.searchBooksByAuthor(author, pageable);
    }

    @GetMapping("/search/description")
    public List<Book> searchBooksByDescription(@RequestParam("description") String description, Pageable pageable) {
        return elasticSearchService.searchBooksByDescription(description, pageable);
    }

    @GetMapping("/search/tag")
    public List<Book> searchBooksByTag(@RequestParam("tag") String tag, Pageable pageable) {
        return elasticSearchService.searchBooksByTag(tag, pageable);
    }

    @GetMapping("/search/isbn")
    public List<Book> searchBooksByIsbn(@RequestParam("isbn") String isbn) {
        return elasticSearchService.searchBooksByIsbn(isbn);
    }

    @GetMapping("/search/sellCount")
    public List<Book> searchBooksBySellCount(Pageable pageable) {
        return elasticSearchService.searchBooksBySellCount(pageable);
    }

}
