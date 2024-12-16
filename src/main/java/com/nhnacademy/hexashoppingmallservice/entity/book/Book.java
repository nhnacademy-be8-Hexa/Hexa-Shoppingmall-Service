package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false,length = 100)
    private String bookTitle;

    @Column(nullable = false, columnDefinition = "TEXT") // 명시적으로 mysql의 text로 지정
    @Lob // 대량 데이터를 저장할 수 있는 필드로 지정
    private String bookDescription;

    @Column(nullable = false)
    private LocalDate bookPubDate;

    @Column(nullable = false)
    private Long bookISBN;

    @Column(nullable = false)
    private int bookOriginPrice;

    @Column(nullable = false)
    private int bookPrice;

    @Column(nullable = false)
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

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookCategory> bookCategories;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookTag> bookTags;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors;
}
