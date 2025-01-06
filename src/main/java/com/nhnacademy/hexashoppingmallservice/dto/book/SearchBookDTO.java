package com.nhnacademy.hexashoppingmallservice.dto.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nhnacademy.hexashoppingmallservice.document.Author;
import com.nhnacademy.hexashoppingmallservice.document.BookStatus;
import com.nhnacademy.hexashoppingmallservice.document.Publisher;
import com.nhnacademy.hexashoppingmallservice.document.Tag;
import java.util.List;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class SearchBookDTO {
    private Long bookId;
    private String bookTitle;
    private String bookDescription;
    private List<Author> authors;
    private List<Tag> tags;
    private Publisher publisher;
    private BookStatus bookStatus;
    private Long bookIsbn;
    private String bookPubDate;
    private int bookOriginPrice;
    private int bookPrice;
    private boolean bookWrappable;
    private int bookView;
    private int bookAmount;
    private Long bookSellCount;
}
