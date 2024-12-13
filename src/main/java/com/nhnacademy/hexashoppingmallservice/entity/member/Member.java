package com.nhnacademy.hexashoppingmallservice.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Member {
    @Id
    @Length(max = 20)
    private String memberId;
    @Column(nullable = false)
    @Length(max = 60)
    private String memberPassword;
    @Column(nullable = false)
    @Length(max = 20)
    private String memberName;
    @Column(nullable = false, name = "member_phonenumber")
    @Length(max = 11)
    private String memberNumber;
    @Column
    private LocalDate memberBirthAt;
    @Column(nullable = false)
    private LocalDate memberCreatedAt;
    @Column
    private LocalDateTime memberLastLoginAt;
    @Enumerated(EnumType.STRING)
    private Role memberRole;
    @ManyToOne
    @JoinColumn(name = "rating_id")
    private Rating rating;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private MemberStatus memberStatus;
}
