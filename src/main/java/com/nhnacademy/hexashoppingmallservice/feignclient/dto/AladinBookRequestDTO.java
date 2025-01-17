package com.nhnacademy.hexashoppingmallservice.feignclient.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AladinBookRequestDTO {
    @NotBlank
    @Size(max = 100)
    String title;

    @NotEmpty
    List<String> authors;

    @NotNull
    @Positive
    int priceSales;

    @NotNull
    @Positive
    int priceStandard;

    @NotBlank
    @Size(max = 20)
    String publisher;

    @NotNull
    String bookStatusId;

    @PastOrPresent
    LocalDate pubDate;

    @NotNull
    @Digits(integer = 13, fraction = 0)
    Long isbn13;

    @NotBlank
    @Size(min = 10, max = 10000)
    String description;

    @NotNull
    boolean bookWrappable;

    @PositiveOrZero
    int bookAmount;
}
