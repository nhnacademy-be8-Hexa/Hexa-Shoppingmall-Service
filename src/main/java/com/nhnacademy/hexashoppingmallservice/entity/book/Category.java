package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false,length = 20)
    @Setter
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @Builder
    private Category (String categoryName, Category parentCategory){
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
    }

    public static Category of(String categoryName, Category parentCategory){
        return Category.builder()
                .categoryName(categoryName)
                .parentCategory(parentCategory)
                .build();
    }
}
