package com.nhnacademy.hexashoppingmallservice.dto.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nhnacademy.hexashoppingmallservice.document.BookStatus;
import com.nhnacademy.hexashoppingmallservice.document.Publisher;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class SearchBookDTO {
    private Long bookId;
    private String bookTitle;
    private String bookDescription;
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
