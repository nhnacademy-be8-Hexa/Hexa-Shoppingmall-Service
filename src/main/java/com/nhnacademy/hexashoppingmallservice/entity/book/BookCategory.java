package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCategoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private Category category;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull
    private Book book;

    @Builder
    private BookCategory(Category category, Book book) {
        this.category = category;
        this.book = book;
    }

    public static BookCategory of(Category category, Book book){
        return BookCategory.builder()
                .category(category)
                .book(book)
                .build();
    }
}
