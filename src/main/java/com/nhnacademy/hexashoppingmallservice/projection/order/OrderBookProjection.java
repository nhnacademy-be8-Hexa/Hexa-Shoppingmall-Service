package com.nhnacademy.hexashoppingmallservice.projection.order;

public interface OrderBookProjection {

    Long getOrderId();        // 주문 ID
    Long getBookId();         // 책 ID
    String getBookTitle();    // 책 제목
    Integer getOrderBookAmount();  // 주문 수량
    Integer getBookPrice();   // 책 가격
    Long getCouponId();       // 쿠폰 ID
}
