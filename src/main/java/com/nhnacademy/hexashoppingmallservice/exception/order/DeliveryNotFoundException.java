package com.nhnacademy.hexashoppingmallservice.exception.order;

public class DeliveryNotFoundException extends RuntimeException {
    public DeliveryNotFoundException(String message) {
        super(message);
    }
}
