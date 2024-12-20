package com.nhnacademy.hexashoppingmallservice.projection.member.order;

public interface OrderProjection {
    Long getOrderPrice();

    String getOrderedAt();

    String getZoneCode();

    String getAddress();

    String getAddressDetail();
}
