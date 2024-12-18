package com.nhnacademy.hexashoppingmallservice.exception.cartException;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
