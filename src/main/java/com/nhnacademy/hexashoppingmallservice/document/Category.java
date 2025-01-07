package com.nhnacademy.hexashoppingmallservice.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Category {
    private Long categoryId;
    private String categoryName;
    private Category parentCategory;
    
    public static Category of(com.nhnacademy.hexashoppingmallservice.entity.book.Category category,
                              Category parentCategory) {
        return Category.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .parentCategory(parentCategory)
                .build();
    }
}
