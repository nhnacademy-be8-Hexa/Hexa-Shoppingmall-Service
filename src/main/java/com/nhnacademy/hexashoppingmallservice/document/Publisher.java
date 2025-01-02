package com.nhnacademy.hexashoppingmallservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {
    private Long publisherId;
    private String publisherName;

    public static Publisher of(Long publisherId, String publisherName) {
        return Publisher.builder()
                .publisherId(publisherId)
                .publisherName(publisherName)
                .build();
    }
}
