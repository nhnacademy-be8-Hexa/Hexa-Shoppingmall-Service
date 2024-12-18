package com.nhnacademy.hexashoppingmallservice.exception.cartException;

public class CartAlreadyExistException extends RuntimeException {
    public CartAlreadyExistException(String message) {
        super(message);
    }
}
