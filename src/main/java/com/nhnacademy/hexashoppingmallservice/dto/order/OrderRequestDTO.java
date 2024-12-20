package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 5)
    private String zoneCode;

    @Length(max = 50)
    private String address;

    @NotNull
    @Length(max = 100)
    private String addressDetail;
}
