package com.nhnacademy.hexashoppingmallservice.projection.review;

import java.math.BigDecimal;

public interface ReviewProjection {
    Long getReviewId();
    String getReviewContent();
    BigDecimal getReviewRating();
    MemberProjection getMember();
    Boolean isReviewIsblocked();

    interface MemberProjection {
        String getMemberId();
    }
}
