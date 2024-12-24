package com.nhnacademy.hexashoppingmallservice.projection.order;

import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;

import java.time.LocalDateTime;

public interface OrderProjection {
    Long getOrderId();
    Integer getOrderPrice();
    LocalDateTime getOrderedAt();
    WrappingPaper getWrappingPaper();
    OrderStatus getOrderStatus();
    String getZoneCode();
    String getAddress();
    String getAddressDetail();
    MemberProjection getMember(); // 중첩 프로젝션으로 Member 정보를 반환

    interface MemberProjection {
        String getMemberId();
    }
}
