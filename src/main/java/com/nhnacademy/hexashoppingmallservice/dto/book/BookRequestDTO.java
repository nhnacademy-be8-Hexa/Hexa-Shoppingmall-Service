package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String bookTitle;

    @NotBlank
    @Size(min = 10, max = 10000)
    private String bookDescription;

    private LocalDate bookPubDate;

    @NotNull
    @Digits(integer = 13,fraction = 0)
    private Long bookIsbn;

    @NotNull
    @Positive
    private int bookOriginPrice;

    @NotNull
    @Positive
    private int bookPrice;

    @NotNull
    private boolean bookWrappable;

    @NotNull
    private String publisherId;

    @NotNull
    private String bookStatusId;


}
