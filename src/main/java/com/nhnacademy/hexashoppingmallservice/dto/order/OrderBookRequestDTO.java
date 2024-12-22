package com.nhnacademy.hexashoppingmallservice.dto.order;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class OrderBookRequestDTO {


    @Column(nullable = false)
    private Integer orderBookAmount;

    @NotNull
    @Column(columnDefinition = "bigint")
    private Long couponId;

}
