package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnsRequestDTO {

    private Long orderId;   //pk이자 fk
    private Long returnsReasonId; //fk
    private String returnsDetail;
}
