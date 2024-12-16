package com.nhnacademy.hexashoppingmallservice.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberStatusRequestDTO {
    @NotNull
    private String statusName;
}
