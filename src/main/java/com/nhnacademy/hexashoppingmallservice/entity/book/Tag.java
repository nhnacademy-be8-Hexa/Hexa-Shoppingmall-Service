package com.nhnacademy.hexashoppingmallservice.entity.book;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(nullable = false, unique = true, length = 30)
    @Setter
    private String tagName;

    @Builder
    private Tag(String tagName) {
        this.tagName = tagName;
    }

    public static Tag of(String tagName) {
        return Tag.builder()
                .tagName(tagName)
                .build();
    }
}
