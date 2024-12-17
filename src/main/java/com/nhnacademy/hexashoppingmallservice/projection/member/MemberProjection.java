package com.nhnacademy.hexashoppingmallservice.projection.member;

import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.member.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MemberProjection {
    String getMemberId();
    String getMemberName();
    String getMemberNumber();
    String getMemberEmail();
    LocalDate getMemberBirthAt();
    LocalDateTime getMemberCreatedAt();
    LocalDateTime getMemberLastLoginAt();
    Role getMemberRole();
    Rating getRating();
    MemberStatus getMemberStatus();
}
