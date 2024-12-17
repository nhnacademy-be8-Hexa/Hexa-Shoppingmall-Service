package com.nhnacademy.hexashoppingmallservice.entity.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
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


}
