package com.nhnacademy.hexashoppingmallservice.feignclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookIsbnAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.book.PublisherNotFoundException;
import com.nhnacademy.hexashoppingmallservice.feignclient.AladinApi;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.ListBook;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AladinApiService {

    private final AladinApi aladinApi;
    private final String ttbkey = "ttb30decade2030001";
    private final String output = "JS";
    private final String version = "20131101";
    private final ObjectMapper objectMapper;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BookStatusRepository bookStatusRepository;

    @Autowired
    public AladinApiService(AladinApi aladinApi, ObjectMapper objectMapper,
                            BookRepository bookRepository,
                            PublisherRepository publisherRepository,
                            BookStatusRepository bookStatusRepository) {
        this.aladinApi = aladinApi;
        this.objectMapper = objectMapper;
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.bookStatusRepository = bookStatusRepository;
    }

    public List<Book> searchBooks(String query, Long bookStatusId, Long publisherId) {
        try {
            ResponseEntity<String> response = aladinApi.searchBooks(ttbkey, query, output, version);
            ListBook books = objectMapper.readValue(response.getBody(), ListBook.class);
            List<Book> items = books.getItem();
            
            for (Book item : items) {

                if (bookRepository.existsByBookIsbn(Long.valueOf(item.getIsbn13()))) {
                    throw new BookIsbnAlreadyExistException("isbn - %s already exist ".formatted(item.getIsbn13()));
                }

                if (!publisherRepository.existsById(publisherId)) {
                    throw new PublisherNotFoundException("publisher - %s not found".formatted(publisherId));
                }
                Publisher publisher = publisherRepository.findById(publisherId).orElseThrow();

                if (!bookStatusRepository.existsById(bookStatusId)) {
                    throw new BookStatusNotFoundException("bookStatusId - %s not found".formatted(bookStatusId));
                }

                BookStatus bookStatus = bookStatusRepository.findById(bookStatusId).orElseThrow();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                com.nhnacademy.hexashoppingmallservice.entity.book.Book book =
                        com.nhnacademy.hexashoppingmallservice.entity.book.Book.of(
                                item.getTitle(),
                                item.getDescription(),
                                LocalDate.parse(item.getPubDate(), formatter),
                                Long.valueOf(item.getIsbn13()),
                                Integer.parseInt(item.getPriceSales()),
                                Integer.parseInt(item.getPriceStandard()),
                                publisher,
                                bookStatus
                        );
                book.setBookSellCount(Long.valueOf(item.getSalesPoint()));
                bookRepository.save(book);
            }

            return items;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
