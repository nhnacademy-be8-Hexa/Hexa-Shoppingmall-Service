package com.nhnacademy.hexashoppingmallservice.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Member {
    @Id
    @Length(max = 20)
    private String memberId;
    @Column(nullable = false)
    @Length(max = 60)
    @Setter
    private String memberPassword;
    @Column(nullable = false)
    @Length(max = 20)
    private String memberName;
    @Column(nullable = false, name = "member_phonenumber", unique = true)
    @Length(max = 11)
    @Setter
    private String memberNumber;
    @Column(unique = true)
    @Length(max = 320)
    private String memberEmail;
    @Column
    @Setter
    private LocalDate memberBirthAt;
    @Column(nullable = false)
    private LocalDateTime memberCreatedAt;
    @Column
    @Setter
    private LocalDateTime memberLastLoginAt;
    @Enumerated(EnumType.STRING)
    private Role memberRole;
    @ManyToOne
    @JoinColumn(name = "rating_id", nullable = false)
    @Setter
    private Rating rating;
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    @Setter
    private MemberStatus memberStatus;

    public static Member of(String memberId, String memberPassword, String memberName, String memberNumber, String memberEmail, LocalDate memberBirthAt, Rating rating, MemberStatus memberStatus) {
        return Member.builder()
                .memberId(memberId)
                .memberPassword(memberPassword)
                .memberName(memberName)
                .memberNumber(memberNumber)
                .memberEmail(memberEmail)
                .memberBirthAt(memberBirthAt)
                .memberCreatedAt(LocalDateTime.now())
                .memberRole(Role.MEMBER)
                .rating(rating)
                .memberStatus(memberStatus)
                .build();
    }

    public void login(){
        this.memberLastLoginAt = LocalDateTime.now();
    }
}
