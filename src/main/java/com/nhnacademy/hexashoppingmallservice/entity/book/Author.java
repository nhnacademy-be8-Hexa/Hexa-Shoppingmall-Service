package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String authorName;

    @Builder
    private Author(String authorName) {
        this.authorName = authorName;
    }

    public static Author of(String authorName) {
        return Author.builder()
                .authorName(authorName)
                .build();
    }
}