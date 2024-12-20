package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WrappingPaperRequestDTO {
    @NotNull
    private String wrappingPaperName;
    @NotNull
    private Integer wrappingPaperPrice;
}
