package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class BookAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookAuthorId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull
    private Author author;

    @Builder
    private BookAuthor(Book book, Author author) {
        this.book = book;
        this.author = author;
    }

    public static BookAuthor of(Book book, Author author){
        return BookAuthor.builder()
                .book(book)
                .author(author)
                .build();
    }
}
