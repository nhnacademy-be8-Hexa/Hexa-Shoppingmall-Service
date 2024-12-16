package com.nhnacademy.hexashoppingmallservice.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDTO {
    private String memberId;
    private String memberPassword;
    private String memberName;
    private String memberNumber;
    private String memberEmail;
    private LocalDate memberBirthAt;
    private LocalDate memberCreatedAt;
    private LocalDateTime memberLastLoginAt;
    private String memberRole;
    private String ratingId;
    private String statusId;
}
