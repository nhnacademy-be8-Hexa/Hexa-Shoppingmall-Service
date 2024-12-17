package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @Setter
    private String bookTitle;

    @NotBlank
    @Lob
    @Column(nullable = false, columnDefinition = "text")
    @Setter
    private String bookDescription;

    @NotNull
    @PastOrPresent
    @Column(nullable = false, name = "book_pubdate")
    private LocalDate bookPubDate;

    @NotNull
    @Digits(integer = 13, fraction = 0)
    @Column(nullable = false, unique = true)
    @Setter
    private Long bookIsbn;

    @Positive
    @Column(nullable = false)
    private int bookOriginPrice;

    @Positive
    @Column(nullable = false)
    @Setter
    private int bookPrice;

    @NotNull
    @Column(nullable = false)
    @Setter
    private Boolean bookWrappable = false;

    @PositiveOrZero
    @Column(nullable = false)
    private int bookView = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private int bookAmount = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Long bookSellCount = 0L;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @ManyToOne
    @JoinColumn(name = "book_status_id", nullable = false)
    private BookStatus bookStatus;

    // 필수 필드만 포함한 Builder 추가
    @Builder
    private Book(String bookTitle, String bookDescription, LocalDate bookPubDate, Long bookIsbn, int bookOriginPrice, int bookPrice, Publisher publisher, BookStatus bookStatus) {
        this.bookTitle = bookTitle;
        this.bookDescription = bookDescription;
        this.bookPubDate = bookPubDate;
        this.bookIsbn = bookIsbn;
        this.bookOriginPrice = bookOriginPrice;
        this.bookPrice = bookPrice;
        this.publisher = publisher;
        this.bookStatus = bookStatus;

        // 초기값 설정
        this.bookWrappable = false;
        this.bookView = 0;
        this.bookAmount = 0;
        this.bookSellCount = 0L;
    }

    // 간편한 객체 생성을 위한 of() 메서드 추가
    public static Book of(String bookTitle, String bookDescription, LocalDate bookPubDate, Long bookIsbn, int bookOriginPrice, int bookPrice, Publisher publisher, BookStatus bookStatus) {
        return Book.builder()
                .bookTitle(bookTitle)
                .bookDescription(bookDescription)
                .bookPubDate(bookPubDate)
                .bookIsbn(bookIsbn)
                .bookOriginPrice(bookOriginPrice)
                .bookPrice(bookPrice)
                .publisher(publisher)
                .bookStatus(bookStatus)
                .build();
    }
}