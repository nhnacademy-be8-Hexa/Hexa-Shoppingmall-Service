package com.nhnacademy.hexashoppingmallservice.feignclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookAuthor;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookIsbnAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.feignclient.AladinApi;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.ListBook;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import com.nhnacademy.hexashoppingmallservice.repository.elasticsearch.ElasticSearchRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AladinApiService {

    private final AladinApi aladinApi;
    private final String ttbkey = "ttb30decade2030001";
    private final String output = "JS";
    private final String version = "20131101";
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BookStatusRepository bookStatusRepository;
    private final AuthorRepository authorRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final ElasticSearchRepository elasticSearchRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AladinApiService(AladinApi aladinApi,
                            BookRepository bookRepository,
                            PublisherRepository publisherRepository,
                            BookStatusRepository bookStatusRepository,
                            AuthorRepository authorRepository,
                            BookAuthorRepository bookAuthorRepository,
                            ElasticSearchRepository elasticSearchRepository) {
        this.aladinApi = aladinApi;
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.authorRepository = authorRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.elasticSearchRepository = elasticSearchRepository;
        this.objectMapper = new ObjectMapper()
                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<Book> searchBooks(String query) {
        try {
            ResponseEntity<String> response = aladinApi.searchBooks(ttbkey, query, output, version);
            ListBook books = objectMapper.readValue(response.getBody(), ListBook.class);
            return books.getItem();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<Book> createBooks(String query, Long bookStatusId) {
        try {
            ResponseEntity<String> response = aladinApi.searchBooks(ttbkey, query, output, version);
            ListBook books = objectMapper.readValue(response.getBody(), ListBook.class);
            List<Book> items = books.getItem();

            for (Book item : items) {
                String description = item.getDescription();
                

                if (bookRepository.existsByBookIsbn(Long.valueOf(item.getIsbn13()))) {
                    throw new BookIsbnAlreadyExistException("isbn - %s already exist ".formatted(item.getIsbn13()));
                }

                Publisher publisher = publisherRepository.findByPublisherName(item.getPublisher().trim());

                if (Objects.isNull(publisher)) {
                    publisher = Publisher.of(item.getPublisher());
                    publisherRepository.save(publisher);
                }


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

                String line = item.getAuthor();
                String cleanedLine = line.replaceAll("\\(.*?\\)", "").trim();
                String[] authorNames = cleanedLine.split(",");
                Author author;

                for (String authorName : authorNames) {
                    if (Objects.nonNull(authorName) && !authorName.isBlank()) {
                        author = authorRepository.findByAuthorName(authorName.trim());
                        if (Objects.isNull(author)) {
                            author = Author.of(authorName.trim());
                            authorRepository.save(author);
                        }
                        System.out.println(author.getAuthorId());
                        BookAuthor bookAuthor = BookAuthor.of(book, author);
                        bookAuthorRepository.save(bookAuthor);
                    }
                }
            }

            return items;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
