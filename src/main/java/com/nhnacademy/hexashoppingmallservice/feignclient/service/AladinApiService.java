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
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.ListBook;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AladinApiService {

    private final AladinApi aladinApi;
    private final String ttbKey = "ttb30decade2030001";
    private final String output = "JS";
    private final String version = "20131101";
    private final ObjectMapper objectMapper;

    private final BookRepository bookRepository;
    private final BookStatusRepository bookStatusRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final BookAuthorRepository bookAuthorRepository;


    @Autowired
    public AladinApiService(AladinApi aladinApi,
                            BookRepository bookRepository,
                            BookStatusRepository bookStatusRepository,
                            PublisherRepository publisherRepository,
                            AuthorRepository authorRepository,
                            BookAuthorRepository bookAuthorRepository) {
        this.aladinApi = aladinApi;
        this.objectMapper = new ObjectMapper()
                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.bookRepository = bookRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
        this.bookAuthorRepository = bookAuthorRepository;
    }

    public List<AladinBookDTO> searchBooks(String query) {
        try {
            ResponseEntity<String> response = aladinApi.searchBooks(ttbKey, query, output, version);
            ListBook books = objectMapper.readValue(response.getBody(), ListBook.class);


            List<Book> bookList = books.getItem();
            List<AladinBookDTO> aladinBooks = new ArrayList<>();

            if (Objects.isNull(bookList)) {
                return Collections.emptyList();
            }
            for (Book book : bookList) {
                String title = book.getTitle();
                String description = book.getDescription();
                String decodedTitle = Jsoup.parse(title).text();
                String decodedDescription = Jsoup.parse(description).text();

                String cleanedTitle = decodedTitle.split("-")[0].trim();

                String line = book.getAuthor();
                String cleanedLine = line.replaceAll("\\(.*?\\)", "").trim();
                List<String> authors = Arrays.stream(cleanedLine.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                String date = book.getPubDate();
                LocalDate pubDate = null;
                if (Objects.nonNull(date) && !date.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    pubDate = LocalDate.parse(book.getPubDate(), formatter);
                }

                String isbn = book.getIsbn13();
                Long isbn13 = null;
                if (Objects.nonNull(isbn) && !isbn.isEmpty()) {
                    isbn13 = Long.parseLong(isbn);
                }

                String sales = book.getPriceSales();
                int priceSales = 0;
                if (Objects.nonNull(sales) && !sales.isEmpty()) {
                    priceSales = Integer.parseInt(sales);
                }

                String standard = book.getPriceStandard();
                int priceStandard = 0;
                if (Objects.nonNull(standard) && !standard.isEmpty()) {
                    priceStandard = Integer.parseInt(standard);
                }

                String point = book.getSalesPoint();
                Long salesPoint = null;
                if (Objects.nonNull(point) && !point.isEmpty()) {
                    salesPoint = Long.parseLong(point);
                }

                AladinBookDTO aladinBook = new AladinBookDTO();
                aladinBook.setTitle(cleanedTitle);
                aladinBook.setAuthors(authors);
                aladinBook.setPriceSales(priceSales);
                aladinBook.setPriceStandard(priceStandard);
                aladinBook.setPublisher(book.getPublisher());
                aladinBook.setPubDate(pubDate);
                aladinBook.setIsbn13(isbn13);
                aladinBook.setDescription(decodedDescription);
                aladinBook.setSalesPoint(salesPoint);
                aladinBook.setCover(book.getCover().replace("coversum", "cover500"));


                aladinBooks.add(aladinBook);

            }
            return aladinBooks;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public com.nhnacademy.hexashoppingmallservice.entity.book.Book createAladinBook(
            AladinBookRequestDTO aladinBookRequestDTO) {

        Publisher publisher = Publisher.of(aladinBookRequestDTO.getPublisher());
        if (Objects.nonNull(publisherRepository.findByPublisherName(aladinBookRequestDTO.getPublisher()))) {
            publisher = publisherRepository.findByPublisherName(aladinBookRequestDTO.getPublisher());
        } else {
            publisher = publisherRepository.save(publisher);
        }


        if (!bookStatusRepository.existsById(Long.parseLong(aladinBookRequestDTO.getBookStatusId()))) {
            throw new BookStatusNotFoundException(
                    "status id - %s is not found".formatted(aladinBookRequestDTO.getBookStatusId()));
        }

        BookStatus bookStatus =
                bookStatusRepository.findById(Long.parseLong(aladinBookRequestDTO.getBookStatusId())).orElseThrow();

        // isbn 중복 체크
        if (bookRepository.existsByBookIsbn(aladinBookRequestDTO.getIsbn13())) {
            throw new BookIsbnAlreadyExistException(
                    "isbn - %d already exist ".formatted(aladinBookRequestDTO.getIsbn13()));
        }

        com.nhnacademy.hexashoppingmallservice.entity.book.Book
                book = com.nhnacademy.hexashoppingmallservice.entity.book.Book.of(
                aladinBookRequestDTO.getTitle(),
                aladinBookRequestDTO.getDescription(),
                aladinBookRequestDTO.getPubDate(),
                aladinBookRequestDTO.getIsbn13(),
                aladinBookRequestDTO.getPriceStandard(),
                aladinBookRequestDTO.getPriceSales(),
                publisher,
                bookStatus
        );

        book.setBookAmount(aladinBookRequestDTO.getBookAmount());
        book.setBookWrappable(aladinBookRequestDTO.isBookWrappable());
        book = bookRepository.save(book);

        for (String authorName : aladinBookRequestDTO.getAuthors()) {
            Author author = Author.of(authorName);
            if (Objects.isNull(authorRepository.findByAuthorName(authorName))) {
                author = authorRepository.save(author);
            } else {
                author = authorRepository.findByAuthorName(authorName);
            }

            BookAuthor bookAuthor = BookAuthor.of(book, author);
            bookAuthorRepository.save(bookAuthor);
        }

        return book;
    }

}
