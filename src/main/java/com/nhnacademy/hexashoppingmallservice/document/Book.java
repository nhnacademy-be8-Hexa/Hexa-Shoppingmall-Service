package com.nhnacademy.hexashoppingmallservice.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "book31")
public class Book {
    @Id
    private Long bookId;
    private String bookTitle;
    private String bookDescription;

    @Field(type = FieldType.Nested)
    private List<Author> authors;

    private String publisherName;
    private String bookStatusName;
    private String tagName;
    
    //    private long isbn
//    private LocalDate bookPubDate;
//    private int bookOriginPrice;
//    private int bookPrice;
//    private boolean bookWrappable;
//    private int bookView;
//    private int bookAmount;
    private long bookSellCount;

    public Book() {
    }

    public Book(Long bookId, String bookTitle, String bookDescription, List<Author> authors, String publisherName,
                String bookStatusName, String tagName, long bookSellCount) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookDescription = bookDescription;
        this.authors = authors;
        this.publisherName = publisherName;
        this.bookStatusName = bookStatusName;
        this.tagName = tagName;
        this.bookSellCount = bookSellCount;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getBookStatusName() {
        return bookStatusName;
    }

    public String getTagName() {
        return tagName;
    }

    public long getBookSellCount() {
        return bookSellCount;
    }
}


