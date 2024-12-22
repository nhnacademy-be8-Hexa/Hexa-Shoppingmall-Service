package com.nhnacademy.hexashoppingmallservice.exception.order;

public class OrderBookNotFoundException extends RuntimeException {
    public OrderBookNotFoundException(String message) {
        super(message);
    }
}
