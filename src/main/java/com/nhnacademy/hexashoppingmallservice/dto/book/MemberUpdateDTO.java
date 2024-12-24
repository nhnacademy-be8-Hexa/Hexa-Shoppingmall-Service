package com.nhnacademy.hexashoppingmallservice.dto.book;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberUpdateDTO {
    private String memberPassword;
    private String memberName;
    private String memberNumber;
    private String memberEmail;
    private LocalDate memberBirthAt;
    private LocalDateTime memberLastLoginAt;
    private String ratingId;
    private String statusId;
}
