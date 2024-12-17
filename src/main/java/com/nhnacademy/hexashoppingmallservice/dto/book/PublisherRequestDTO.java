package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublisherRequestDTO {
    @NotBlank(message = "publisherName cannot be blank")
    @Size(max = 20)
    private String publisherName;
}
