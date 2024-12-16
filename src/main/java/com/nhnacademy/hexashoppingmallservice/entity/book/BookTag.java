package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookTagId;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Builder
    private BookTag(Book book, Tag tag) {
        this.book = book;
        this.tag = tag;
    }

    public static BookTag of(Book book, Tag tag) {
        return BookTag.builder()
                .book(book)
                .tag(tag)
                .build();
    }
}
