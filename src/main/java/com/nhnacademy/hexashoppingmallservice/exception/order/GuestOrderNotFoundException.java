package com.nhnacademy.hexashoppingmallservice.exception.order;

public class GuestOrderNotFoundException extends RuntimeException {
    public GuestOrderNotFoundException(String message) {
        super(message);
    }
}
