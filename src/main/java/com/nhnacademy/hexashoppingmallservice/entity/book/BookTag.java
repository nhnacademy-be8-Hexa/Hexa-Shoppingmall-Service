package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookTagId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "book_tag_ibfk_1"))
    private Book book;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false, foreignKey = @ForeignKey(name = "book_tag_ibfk_2"))
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
