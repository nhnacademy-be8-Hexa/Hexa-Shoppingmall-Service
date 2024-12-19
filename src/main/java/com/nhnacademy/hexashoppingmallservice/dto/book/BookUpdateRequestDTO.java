package com.nhnacademy.hexashoppingmallservice.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateRequestDTO {
    private String bookTitle;
    private String bookDescription;
    private int bookPrice;
    private Boolean bookWrappable;
    private String statusId;
}
