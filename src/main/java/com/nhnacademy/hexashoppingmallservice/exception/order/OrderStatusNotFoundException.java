package com.nhnacademy.hexashoppingmallservice.exception.order;

public class OrderStatusNotFoundException extends RuntimeException {
    public OrderStatusNotFoundException(String message) {
        super(message);
    }
}
