package com.nhnacademy.hexashoppingmallservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RatingRequestDTO {
    private String ratingName;
    private Integer ratingPercent;
}
