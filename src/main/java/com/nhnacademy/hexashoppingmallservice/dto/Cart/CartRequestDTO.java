package com.nhnacademy.hexashoppingmallservice.dto.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartRequestDTO {
    private int cartId;
    private int cartAmount;
    private Long memberId;
    private Long bookId;
}
