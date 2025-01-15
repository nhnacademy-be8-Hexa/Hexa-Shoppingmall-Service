package com.nhnacademy.hexashoppingmallservice.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long bookId;
    private Integer cartAmount;
}
