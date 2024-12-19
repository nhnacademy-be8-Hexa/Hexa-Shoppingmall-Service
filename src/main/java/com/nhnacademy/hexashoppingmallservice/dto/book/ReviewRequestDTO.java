package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {
    @NotNull
    @Length(max = 400)
    private String reviewContent;

    @NotNull
    private BigDecimal reviewRating;
}
