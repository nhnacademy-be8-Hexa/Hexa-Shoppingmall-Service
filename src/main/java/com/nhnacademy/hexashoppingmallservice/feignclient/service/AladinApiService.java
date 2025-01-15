package com.nhnacademy.hexashoppingmallservice.feignclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.feignclient.AladinApi;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.Book;
import com.nhnacademy.hexashoppingmallservice.feignclient.domain.aladin.ListBook;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookDTO;
import com.nhnacademy.hexashoppingmallservice.repository.book.AuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookAuthorRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import com.nhnacademy.hexashoppingmallservice.repository.category.BookCategoryRepository;
import com.nhnacademy.hexashoppingmallservice.repository.category.CategoryRepository;
import com.nhnacademy.hexashoppingmallservice.repository.elasticsearch.ElasticSearchRepository;
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

@Service
public class AladinApiService {

    private final AladinApi aladinApi;
    private final String ttbKey = "ttb30decade2030001";
    private final String output = "JS";
    private final String version = "20131101";
    private final String queryType = "Title";
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BookStatusRepository bookStatusRepository;
    private final AuthorRepository authorRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Autowired
    public AladinApiService(AladinApi aladinApi,
                            BookRepository bookRepository,
                            PublisherRepository publisherRepository,
                            BookStatusRepository bookStatusRepository,
                            AuthorRepository authorRepository,
                            BookAuthorRepository bookAuthorRepository,
                            ElasticSearchRepository elasticSearchRepository,
                            CategoryRepository categoryRepository,
                            BookCategoryRepository bookCategoryRepository) {
        this.aladinApi = aladinApi;
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.authorRepository = authorRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.objectMapper = new ObjectMapper()
                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }

    public List<AladinBookDTO> searchBooks(String query) {
        try {
            ResponseEntity<String> response = aladinApi.searchBooks(ttbKey, query, output, version, queryType);
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

                AladinBookDTO aladinBook = new AladinBookDTO();
                aladinBook.setTitle(cleanedTitle);
                aladinBook.setAuthors(authors);
                aladinBook.setPriceSales(book.getPriceSales());
                aladinBook.setPriceStandard(book.getPriceStandard());
                aladinBook.setPublisher(book.getPublisher());
                aladinBook.setPubDate(book.getPubDate());
                aladinBook.setIsbn13(book.getIsbn13());
                aladinBook.setDescription(decodedDescription);
                aladinBook.setSalesPoint(book.getSalesPoint());
                aladinBook.setCover(book.getCover().replace("coversum", "cover200"));

                aladinBooks.add(aladinBook);

            }
            return aladinBooks;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//    @Transactional
//    public List<Book> createBooks(String query, Long bookStatusId) {
//        try {
//            ResponseEntity<String> response = aladinApi.searchBooks(ttbKey, query, output, version, queryType);
//            ListBook books = objectMapper.readValue(response.getBody(), ListBook.class);
//            List<Book> items = books.getItem();
//
//
//            for (Book item : items) {
//                String title = item.getTitle();
//                String description = item.getDescription();
//                String decodedTitle = Jsoup.parse(title).text();
//                String decodedDescription = Jsoup.parse(description).text();
//                item.setTitle(decodedTitle);
//                item.setDescription(decodedDescription);
//
//
//                if (bookRepository.existsByBookIsbn(Long.valueOf(item.getIsbn13()))) {
//                    throw new BookIsbnAlreadyExistException("isbn - %s already exist ".formatted(item.getIsbn13()));
//                }
//
//                Publisher publisher = publisherRepository.findByPublisherName(item.getPublisher().trim());
//
//                if (Objects.isNull(publisher)) {
//                    publisher = Publisher.of(item.getPublisher());
//                    publisherRepository.save(publisher);
//                }
//
//
//                if (!bookStatusRepository.existsById(bookStatusId)) {
//                    throw new BookStatusNotFoundException("bookStatusId - %s not found".formatted(bookStatusId));
//                }
//
//                BookStatus bookStatus = bookStatusRepository.findById(bookStatusId).orElseThrow();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//
//                com.nhnacademy.hexashoppingmallservice.entity.book.Book book =
//                        com.nhnacademy.hexashoppingmallservice.entity.book.Book.of(
//                                item.getTitle(),
//                                item.getDescription(),
//                                LocalDate.parse(item.getPubDate(), formatter),
//                                Long.valueOf(item.getIsbn13()),
//                                Integer.parseInt(item.getPriceSales()),
//                                Integer.parseInt(item.getPriceStandard()),
//                                publisher,
//                                bookStatus
//                        );
//                book.setBookSellCount(Long.valueOf(item.getSalesPoint()));
//
//
//                bookRepository.save(book);
//
//                String line = item.getAuthor();
//                String cleanedLine = line.replaceAll("\\(.*?\\)", "").trim();
//                String[] authorNames = cleanedLine.split(",");
//                Author author;
//
//                for (String authorName : authorNames) {
//                    String trimmedAuthorName = authorName.trim();
//                    if (!authorName.isBlank()) {
//                        author = authorRepository.findByAuthorName(trimmedAuthorName);
//                        if (Objects.isNull(author)) {
//                            author = Author.of(trimmedAuthorName);
//                            authorRepository.save(author);
//                        }
//                        BookAuthor bookAuthor = BookAuthor.of(book, author);
//                        bookAuthorRepository.save(bookAuthor);
//                    }
//                }
//
//                String categoryNames = item.getCategoryName();
//                String[] categories = categoryNames.split(">");
//
//                int maxLevel = Math.min(2, categories.length);
//
//                Category parentCategory = null;
//
//                for (int i = 0; i < maxLevel; i++) {
//                    String categoryName = categories[i].trim();
//                    if (categoryName.isBlank()) {
//                        continue;
//                    }
//                    Category category = categoryRepository.findByCategoryName(categoryName);
//                    if (Objects.isNull(category)) {
//                        category = Category.of(categoryName, parentCategory);
//                        categoryRepository.save(category);
//                    }
//                    BookCategory bookCategory = BookCategory.of(category, book);
//                    bookCategoryRepository.save(bookCategory);
//
//                    parentCategory = category;
//
//                }
//
//
//            }
//
//            return items;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


}
