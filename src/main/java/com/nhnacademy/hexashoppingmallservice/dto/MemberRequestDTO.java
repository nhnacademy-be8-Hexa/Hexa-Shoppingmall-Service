package com.nhnacademy.hexashoppingmallservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MemberRequestDTO {
    private String memberId;
    private String memberPassword;
    private String memberName;
    private String memberNumber;
    private LocalDate memberBirthAt;
    private LocalDate memberCreatedAt;
    private LocalDateTime memberLastLoginAt;
    private String memberRole;
    private String ratingId;
    private String statusId;
}
