package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record PointDetailsRequestDTO(@NotNull
                                         Integer pointDetailsIncrement,
                                     @NotNull @NotEmpty @Length(max = 200)
                                     String pointDetailsComment,
                                     @NotNull
                                     LocalDateTime pointDetailsDatetime) {
}
