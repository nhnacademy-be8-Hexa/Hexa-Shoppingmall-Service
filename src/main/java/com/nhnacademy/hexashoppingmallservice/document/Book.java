package com.nhnacademy.hexashoppingmallservice.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Document(indexName = "book31")
public class Book {
    @Id
    private Long bookId;
    private String bookTitle;
    private String bookDescription;
    private List<String> authors;
    private String publisherName;
    private String bookStatusName;
    private String tagName;
    private long isbn;
    private LocalDate bookPubDate;
    private int bookOriginPrice;
    private int bookPrice;
    private boolean bookWrappable;
    private int bookView;
    private int bookAmount;
    private long bookSellCount;

    public Book() {
    }

    public Book(Long bookId, String bookTitle, String bookDescription, List<String> authors, String publisherName,
                String bookStatusName, String tagName, long isbn, LocalDate bookPubDate, int bookOriginPrice,
                int bookPrice,
                boolean bookWrappable, int bookView, int bookAmount, long bookSellCount) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookDescription = bookDescription;
        this.authors = authors;
        this.publisherName = publisherName;
        this.tagName = tagName;
        this.bookStatusName = bookStatusName;
        this.isbn = isbn;
        this.bookPubDate = bookPubDate;
        this.bookOriginPrice = bookOriginPrice;
        this.bookPrice = bookPrice;
        this.bookWrappable = bookWrappable;
        this.bookView = bookView;
        this.bookAmount = bookAmount;
        this.bookSellCount = bookSellCount;
    }
}


