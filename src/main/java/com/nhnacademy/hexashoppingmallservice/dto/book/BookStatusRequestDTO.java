package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookStatusRequestDTO {
    @NotBlank(message = "bookStatus cannot be blank")
    @Size(max = 20)
    private String bookStatus;

}
