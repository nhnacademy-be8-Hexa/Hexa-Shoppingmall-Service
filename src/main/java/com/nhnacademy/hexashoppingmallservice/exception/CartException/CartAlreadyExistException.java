package com.nhnacademy.hexashoppingmallservice.exception.CartException;

public class CartAlreadyExistException extends RuntimeException {
    public CartAlreadyExistException(String message) {
        super(message);
    }
}
