package com.nhnacademy.hexashoppingmallservice.dto.tag;

import jakarta.validation.constraints.NotNull;

public record BookTagRequestDTO(
        @NotNull Long bookId,
        @NotNull Long tagId
) {
}
