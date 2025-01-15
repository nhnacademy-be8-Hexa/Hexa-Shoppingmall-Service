package com.nhnacademy.hexashoppingmallservice.dto.book;

import com.nhnacademy.hexashoppingmallservice.projection.order.OrderBookProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class OrderBookDTO {
    private Long orderId;
    private Long bookId;
    private String bookTitle;
    private Integer orderBookAmount;
    private Integer bookPrice;
    private Long couponId;
}
