package com.nhnacademy.hexashoppingmallservice.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequestDTO {
    @NotNull
    private String ratingName;
    @NotNull
    private Integer ratingPercent;
}
