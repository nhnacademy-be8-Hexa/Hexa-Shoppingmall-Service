package com.nhnacademy.hexashoppingmallservice.entity.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class PointDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointDetailsId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    @Setter
    private Member member;

    @Column(nullable = false)
    private Integer pointDetailsIncrement;

    @Column(nullable = false)
    @Length(max = 200)
    private String pointDetailsComment;

    @Column(nullable = false)
    @Setter
    private LocalDateTime pointDetailsDatetime;
}
