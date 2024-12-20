package com.nhnacademy.hexashoppingmallservice.exception.order;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(String.valueOf(message));
    }
}
