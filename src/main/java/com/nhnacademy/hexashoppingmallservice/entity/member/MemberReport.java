package com.nhnacademy.hexashoppingmallservice.entity.member;

import com.nhnacademy.hexashoppingmallservice.entity.review.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Builder
public class MemberReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberReportId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public static MemberReport of(Member member, Review review) {
        return MemberReport.builder().member(member).review(review).build();
    }
}
