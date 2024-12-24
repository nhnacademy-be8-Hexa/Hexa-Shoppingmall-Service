package com.nhnacademy.hexashoppingmallservice.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "book31")
public class Book {
    @Id
    private Long bookId;
    private String bookTitle;
    private String bookDescription;
    private List<Author> authors;
    private Publisher publisher;
    private BookStatus bookStatus;
    private List<Tag> tags;
    private long isbn;
    private String bookPubDate;
    private int bookOriginPrice;
    private int bookPrice;
    private boolean bookWrappable;
    private int bookView;
    private int bookAmount;
    private long bookSellCount;
    private List<Category> categories;
}


