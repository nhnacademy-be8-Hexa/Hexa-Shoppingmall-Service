package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestDTO {
    @NotBlank
    @NotNull
    private String authorName;

    @NotNull
    private Long bookId;
}
