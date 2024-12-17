package com.nhnacademy.hexashoppingmallservice.entity.review;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Setter
    @Length(max = 400)
    private String reviewContent;

    @Setter
    @Column(precision = 2, scale = 1, nullable = false)
    @NotNull
    private BigDecimal reviewRating;

    @Setter
    @Column(nullable = false)
    @NotNull
    private boolean reviewIsblocked;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "book_id")
    @NotNull
    private Book book;

    @Builder
    public Review(String reviewContent, BigDecimal reviewRating, Member member, Book book) {
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.member = member;
        this.book = book;
    }

    public static Review of(String reviewContent, BigDecimal reviewRating, Member member, Book book) {
        return builder()
                .reviewContent(reviewContent)
                .reviewRating(reviewRating)
                .member(member)
                .book(book)
                .build();
    }
}
