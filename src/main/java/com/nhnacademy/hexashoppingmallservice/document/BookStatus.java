package com.nhnacademy.hexashoppingmallservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookStatus {
    private Long bookStatusId;
    private String bookStatus;

    public static BookStatus of(Long bookStatusId, String bookStatus) {
        return BookStatus.builder()
                .bookStatusId(bookStatusId)
                .bookStatus(bookStatus)
                .build();
    }
}
