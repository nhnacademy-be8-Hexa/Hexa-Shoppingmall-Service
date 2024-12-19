package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private String memberId;
    @NotNull
    private Integer orderPrice;
    private Long wrappingPaperId;
    @NotNull
    private Long orderStatusId;
    @NotNull
    private String zoneCode;
    private String address;
    @NotNull
    private String addressDetail;
}
