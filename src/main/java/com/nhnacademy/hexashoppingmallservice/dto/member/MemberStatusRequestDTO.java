package com.nhnacademy.hexashoppingmallservice.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberStatusRequestDTO {
    private String statusName;
}
