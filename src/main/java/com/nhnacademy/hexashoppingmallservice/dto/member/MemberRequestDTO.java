package com.nhnacademy.hexashoppingmallservice.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDTO {
    @Length(min = 3, max = 20)
    @NotNull
    private String memberId;
    @Length(min = 8, max = 60)
    @NotNull
    private String memberPassword;
    @Length(min = 2, max = 20)
    @NotNull
    private String memberName;
    @Length(min = 10, max = 11)
    @NotNull
    private String memberNumber;
    @Length(max = 320)
    private String memberEmail;
    private LocalDate memberBirthAt;
    private LocalDateTime memberLastLoginAt;
    @NotNull
    private String ratingId;
    @NotNull
    private String statusId;
}
