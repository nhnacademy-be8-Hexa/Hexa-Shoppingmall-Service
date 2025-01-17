package com.nhnacademy.hexashoppingmallservice.feignclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.feignclient.AladinApi;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.ListBook;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.service.book.AuthorService;
import com.nhnacademy.hexashoppingmallservice.service.book.BookService;
import com.nhnacademy.hexashoppingmallservice.service.book.PublisherService;
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
    private final BookService bookService;
    private final AuthorService authorService;
    private final PublisherService publisherService;

    @Autowired
    public AladinApiService(AladinApi aladinApi,
                            BookService bookService,
                            AuthorService authorService,
                            PublisherService publisherService) {
        this.aladinApi = aladinApi;
        this.objectMapper = new ObjectMapper()
                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService = publisherService;
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
                int pricesSales = 0;
                if (Objects.nonNull(sales) && !sales.isEmpty()) {
                    pricesSales = Integer.parseInt(sales);
                }

                String standard = book.getPriceStandard();
                int pricesStandard = 0;
                if (Objects.nonNull(standard) && !standard.isEmpty()) {
                    pricesStandard = Integer.parseInt(standard);
                }

                AladinBookDTO aladinBook = new AladinBookDTO();
                aladinBook.setTitle(cleanedTitle);
                aladinBook.setAuthors(authors);
                aladinBook.setPriceSales(pricesSales);
                aladinBook.setPriceStandard(pricesStandard);
                aladinBook.setPublisher(book.getPublisher());
                aladinBook.setPubDate(pubDate);
                aladinBook.setIsbn13(isbn13);
                aladinBook.setDescription(decodedDescription);
                aladinBook.setSalesPoint(Integer.parseInt(book.getSalesPoint()));
                aladinBook.setCover(book.getCover().replace("coversum", "cover200"));

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
        publisher = publisherService.createPublisher(publisher);

        BookRequestDTO bookRequestDTO = new BookRequestDTO(
                aladinBookRequestDTO.getTitle(),
                aladinBookRequestDTO.getDescription(),
                aladinBookRequestDTO.getPubDate(),
                aladinBookRequestDTO.getIsbn13(),
                aladinBookRequestDTO.getPriceStandard(),
                aladinBookRequestDTO.getPriceSales(),
                false,
                String.valueOf(publisher.getPublisherId()),
                String.valueOf(aladinBookRequestDTO.getBookStatusId()));

        com.nhnacademy.hexashoppingmallservice.entity.book.Book book = bookService.createBook(bookRequestDTO);

        BookUpdateRequestDTO bookUpdateRequestDTO = new BookUpdateRequestDTO(
                null,
                null,
                0,
                aladinBookRequestDTO.isBookWrappable(),
                null
        );
        
        for (String authorName : aladinBookRequestDTO.getAuthors()) {
            authorService.createAuthor(authorName, book.getBookId());
        }

        return bookService.updateBook(book.getBookId(), bookUpdateRequestDTO);
    }

}
