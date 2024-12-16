package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false,length = 100)
    @Setter
    private String bookTitle;

    @Column(nullable = false, columnDefinition = "TEXT") // 명시적으로 mysql의 text로 지정
    @Lob // 대량 데이터를 저장할 수 있는 필드로 지정
    @Setter
    private String bookDescription;

    @Column(nullable = false)
    private LocalDate bookPubDate;

    @Column(nullable = false)
    @Setter
    private Long bookISBN;

    @Column(nullable = false)
    private int bookOriginPrice;

    @Column(nullable = false)
    @Setter
    private int bookPrice;

    @Column(nullable = false)
    @Setter
    private boolean bookWrappable = false;

    @Column(nullable = false , columnDefinition = "INT DEFAULT 0")
    private int bookView = 0;

    @Column(nullable = false , columnDefinition = "INT DEFAULT 0")
    private int bookAmount = 0;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Long bookSellCount = 0L;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToOne
    @JoinColumn(name = "book_status_id")
    private BookStatus bookStatus;

    @Builder
    private Book(String bookTitle,
                 String bookDescription,
                 LocalDate bookPubDate,
                 Long bookISBN,
                 int bookOriginPrice,
                 int bookPrice,
                 boolean bookWrappable,
                 Publisher publisher,
                 BookStatus bookStatus) {
        this.bookTitle = bookTitle;
        this.bookDescription = bookDescription;
        this.bookPubDate = bookPubDate;
        this.bookISBN = bookISBN;
        this.bookOriginPrice = bookOriginPrice;
        this.bookPrice = bookPrice;
        this.bookWrappable = bookWrappable;
        this.publisher = publisher;
        this.bookStatus = bookStatus;

        // 초기 값 설정
        this.bookView = 0;
        this.bookAmount = 0;
        this.bookSellCount = 0L;
    }


    public static Book of(String bookTitle,
                          String bookDescription,
                          LocalDate bookPubDate,
                          Long bookISBN,
                          int bookOriginPrice,
                          int bookPrice,
                          boolean bookWrappable,
                          Publisher publisher,
                          BookStatus bookStatus) {
        return Book.builder()
                .bookTitle(bookTitle)
                .bookDescription(bookDescription)
                .bookPubDate(bookPubDate)
                .bookISBN(bookISBN)
                .bookOriginPrice(bookOriginPrice)
                .bookPrice(bookPrice)
                .bookWrappable(bookWrappable)
                .publisher(publisher)
                .bookStatus(bookStatus)
                .build();
    }

}
