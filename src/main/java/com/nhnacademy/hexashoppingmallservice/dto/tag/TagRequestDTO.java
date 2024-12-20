package com.nhnacademy.hexashoppingmallservice.dto.tag;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record TagRequestDTO(
        @NotBlank @Length(max = 30) String tagName
) {
}
