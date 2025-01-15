package com.nhnacademy.hexashoppingmallservice.repository.querydsl;

import java.math.BigDecimal;

public interface ReviewRepositoryCustom {
    BigDecimal findAverageReviewRatingByBookId(Long bookId);
}
