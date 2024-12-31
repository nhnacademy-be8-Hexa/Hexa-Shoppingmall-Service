package com.nhnacademy.hexashoppingmallservice.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "hexa")
public class Book {
    @Id
    private Long bookId;
    @Setter
    private String bookTitle;
    @Setter
    private String bookDescription;
    @Setter
    private List<String> authorsName;
    @Setter
    private List<String> tagsName;
    @Setter
    private String publisherName;
    @Setter
    private String bookStatus;
    private long bookIsbn;
    private String bookPubDate;
    private int bookOriginPrice;
    @Setter
    private int bookPrice;
    @Setter
    private boolean bookWrappable;
    @Setter
    private int bookView;
    @Setter
    private int bookAmount;
    @Setter
    private long bookSellCount;

    @Builder
    private Book(Long bookId, String bookTitle, String bookDescription, String bookPubDate, Long bookIsbn,
                 int bookOriginPrice, int bookPrice,
                 String publisherName,
                 String bookStatus) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookDescription = bookDescription;
        this.bookPubDate = bookPubDate;
        this.bookIsbn = bookIsbn;
        this.bookOriginPrice = bookOriginPrice;
        this.bookPrice = bookPrice;
        this.publisherName = publisherName;
        this.bookStatus = bookStatus;

        this.bookWrappable = false;
        this.bookView = 0;
        this.bookAmount = 0;
        this.bookSellCount = 0L;
    }


    public static Book of(com.nhnacademy.hexashoppingmallservice.entity.book.Book book) {
        return Book.builder()
                .bookId(book.getBookId())
                .bookTitle(book.getBookTitle())
                .bookDescription(book.getBookDescription())
                .bookPubDate(String.valueOf(book.getBookPubDate()))
                .bookIsbn(book.getBookIsbn())
                .bookOriginPrice(book.getBookOriginPrice())
                .bookPrice(book.getBookPrice())
                .bookSellCount(book.getBookSellCount())
                .publisherName(book.getPublisher().getPublisherName())
                .bookStatus(book.getBookStatus().getBookStatus())
                .build();
    }

}


