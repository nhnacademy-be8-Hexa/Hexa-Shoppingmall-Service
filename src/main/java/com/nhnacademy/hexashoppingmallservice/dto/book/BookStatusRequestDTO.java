package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookStatusRequestDTO {
    @NotNull
    private String bookStatus;

}
