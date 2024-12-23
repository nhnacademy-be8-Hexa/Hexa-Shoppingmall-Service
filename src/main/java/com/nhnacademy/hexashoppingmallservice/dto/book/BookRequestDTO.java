package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Digits(integer = 13, fraction = 0)
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
