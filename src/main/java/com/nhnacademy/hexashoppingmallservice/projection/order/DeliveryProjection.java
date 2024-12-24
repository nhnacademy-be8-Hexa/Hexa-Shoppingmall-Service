package com.nhnacademy.hexashoppingmallservice.projection.order;

import java.time.LocalDateTime;

public interface DeliveryProjection {
    OrderProjection getOrder();
    Integer getDeliveryAmount();
    LocalDateTime getDeliveryDate();
    LocalDateTime getDeliveryReleaseDate();
    interface OrderProjection {
        Long getOrderId();
    }
}
