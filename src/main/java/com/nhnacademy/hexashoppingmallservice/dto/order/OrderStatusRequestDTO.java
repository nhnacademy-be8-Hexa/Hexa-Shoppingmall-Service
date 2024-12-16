package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record OrderStatusRequestDTO(
        @NotBlank @Length(max = 20) String orderStatus
) {
}
