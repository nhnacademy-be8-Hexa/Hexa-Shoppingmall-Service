package com.nhnacademy.hexashoppingmallservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {
    private Long authorId;
    private String authorName;

    public static Author of(Long authorId, String authorName) {
        return Author.builder()
                .authorId(authorId)
                .authorName(authorName)
                .build();
    }
}
