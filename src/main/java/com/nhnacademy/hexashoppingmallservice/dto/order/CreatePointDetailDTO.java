package com.nhnacademy.hexashoppingmallservice.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePointDetailDTO {

    private Integer pointDetailsIncrement;

    private String pointDetailsComment;
}
