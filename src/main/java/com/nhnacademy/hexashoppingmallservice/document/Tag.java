package com.nhnacademy.hexashoppingmallservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    private Long tagId;
    private String tagName;

    public static Tag of(Long tagId, String tagName) {
        return Tag.builder()
                .tagId(tagId)
                .tagName(tagName)
                .build();
    }
}
