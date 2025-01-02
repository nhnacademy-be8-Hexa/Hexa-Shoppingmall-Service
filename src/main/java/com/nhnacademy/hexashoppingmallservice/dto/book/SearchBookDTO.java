package com.nhnacademy.hexashoppingmallservice.dto.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class SearchBookDTO {
    private Long bookId;
    private String bookTitle;
    private String bookDescription;
    private List<String> authorsName;
    private List<String> tagsName;
    private String publisherName;
    private String bookStatus;
    private Long bookIsbn;
    private String bookPubDate;
    private int bookOriginPrice;
    private int bookPrice;
    private boolean bookWrappable;
    private int bookView;
    private int bookAmount;
    private Long bookSellCount;
}
