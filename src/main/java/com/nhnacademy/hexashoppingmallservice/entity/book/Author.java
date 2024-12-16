package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @Setter
    @Column(nullable = false, length = 20)
    private String authorName;

    @Builder
    private Author(String authorName) {
        this.authorName = authorName;
    }

    public static Author of(String authorName){
        return builder()
                .authorName(authorName)
                .build();
    }
}
