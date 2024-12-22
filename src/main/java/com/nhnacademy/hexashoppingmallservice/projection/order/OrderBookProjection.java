package com.nhnacademy.hexashoppingmallservice.projection.order;

public interface OrderBookProjection {
    Long getOrderBookId();
    Integer getOrderBookAmount();
    Long getCouponId();

    OrderProjection getOrder();
    BookProjection getBook();

    interface OrderProjection {
        Long getOrderId();
    }

    interface BookProjection {
        Long getBookId();
    }
}
