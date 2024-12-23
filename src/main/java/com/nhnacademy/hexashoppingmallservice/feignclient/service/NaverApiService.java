package com.nhnacademy.hexashoppingmallservice.feignclient.service;

import com.nhnacademy.hexashoppingmallservice.feignclient.NaverApi;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.naver.Book;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NaverApiService {
    private final NaverApi naverApi;
    private final String clientId = "qxuGyIHCXG9Sv5Vkec38";
    private final String clientSecret = "18vJH6gp1a";
    private final BookRepository bookRepository;
    private final BookStatusRepository bookStatusRepository;
    private final PublisherRepository publisherRepository;

    @Autowired
    public NaverApiService(NaverApi naverApi, BookRepository bookRepository,
                           BookStatusRepository bookStatusRepository,
                           PublisherRepository publisherRepository) {
        this.naverApi = naverApi;
        this.bookRepository = bookRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.publisherRepository = publisherRepository;
    }


    public List<Book> searchBooks(String query) {
        List<Book> items =
                naverApi.searchBooks(clientId, clientSecret, query).getBody().getItems();
        
        return items;
    }
}
