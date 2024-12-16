package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookAuthorId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Builder
    private BookAuthor(Book book, Author author) {
        this.book = book;
        this.author = author;
    }

    public static BookAuthor of(Book book, Author author) {
        return BookAuthor.builder()
                .book(book)
                .author(author)
                .build();
    }
}
