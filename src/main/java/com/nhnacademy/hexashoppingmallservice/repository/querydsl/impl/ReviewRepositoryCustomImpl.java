package com.nhnacademy.hexashoppingmallservice.repository.querydsl.impl;

import com.nhnacademy.hexashoppingmallservice.entity.review.QReview;
import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.ReviewRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ReviewRepositoryCustomImpl extends QuerydslRepositorySupport implements ReviewRepositoryCustom {

    public ReviewRepositoryCustomImpl() {
        super(Review.class);
    }

    @Override
    public BigDecimal findAverageReviewRatingByBookId(Long bookId) {
        QReview review = QReview.review;

        Double res = from(review)
                    .where(review.book.bookId.eq(bookId))
                        .select(review.reviewRating.avg())
                .fetchOne();

        if(res != null) {
            return BigDecimal.valueOf(Math.round(res * 2) / 2.0).setScale(1, RoundingMode.HALF_UP);
        } else{
            return BigDecimal.ZERO;
        }

    }
}
