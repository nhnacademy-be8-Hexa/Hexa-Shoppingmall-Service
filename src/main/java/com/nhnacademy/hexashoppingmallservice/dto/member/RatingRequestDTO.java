package com.nhnacademy.hexashoppingmallservice.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RatingRequestDTO {
    @NotNull
    private String ratingName;
    @NotNull
    private Integer ratingPercent;
}
