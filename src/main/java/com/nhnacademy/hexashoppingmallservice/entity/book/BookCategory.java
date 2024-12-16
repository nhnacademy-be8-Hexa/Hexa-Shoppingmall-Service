package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCategoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "bookd_id",nullable = false)
    private Book book;


    @Builder
    private BookCategory(Category category, Book book){
        this.category = category;
        this.book = book;
    }

    public static BookCategory of(Category category,Book book){
        return BookCategory.builder()
                .category(category)
                .book(book)
                .build();
    }

}
