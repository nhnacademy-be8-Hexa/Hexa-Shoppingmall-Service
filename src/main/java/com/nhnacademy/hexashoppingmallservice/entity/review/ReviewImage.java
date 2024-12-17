package com.nhnacademy.hexashoppingmallservice.entity.review;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCategoryId;

    @Column(nullable = false)
    @NotNull
    @Length(max=50)
    private String reviewImageUrl;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Builder
    public ReviewImage(String reviewImageUrl, Review review) {
        this.reviewImageUrl = reviewImageUrl;
        this.review = review;
    }

    public static ReviewImage of(String reviewImageUrl, Review review) {
        return builder()
                .reviewImageUrl(reviewImageUrl)
                .review(review)
                .build();
    }



}
