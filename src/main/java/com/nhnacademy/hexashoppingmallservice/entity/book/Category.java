package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @Setter
    private Category parentCategory;

    @Builder
    private Category(String categoryName, Category parentCategory) {
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
    }

    public static Category of(String categoryName, Category parentCategory) {
        return Category.builder()
                .categoryName(categoryName)
                .parentCategory(parentCategory)
                .build();
    }
}
