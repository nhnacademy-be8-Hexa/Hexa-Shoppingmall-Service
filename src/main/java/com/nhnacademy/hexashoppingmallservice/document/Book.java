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
    private List<Tag> bookTags;
    @Setter
    private List<Author> authors;
    @Setter
    private Publisher publisher;
    @Setter
    private BookStatus bookStatus;

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
                 Publisher publihser,
                 BookStatus bookstatus) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookDescription = bookDescription;
        this.bookPubDate = bookPubDate;
        this.bookIsbn = bookIsbn;
        this.bookOriginPrice = bookOriginPrice;
        this.bookPrice = bookPrice;
        this.publisher = publihser;
        this.bookStatus = bookstatus;

        this.bookWrappable = false;
        this.bookView = 0;
        this.bookAmount = 0;
        this.bookSellCount = 0L;
    }


    public static Book of(com.nhnacademy.hexashoppingmallservice.entity.book.Book book,
                          BookStatus bookStatus,
                          Publisher publisher) {
        return Book.builder()
                .bookId(book.getBookId())
                .bookTitle(book.getBookTitle())
                .bookDescription(book.getBookDescription())
                .bookPubDate(String.valueOf(book.getBookPubDate()))
                .bookIsbn(book.getBookIsbn())
                .bookOriginPrice(book.getBookOriginPrice())
                .bookPrice(book.getBookPrice())
                .bookSellCount(book.getBookSellCount())
                .publisher(publisher)
                .bookStatus(bookStatus)
                .build();
    }

}


