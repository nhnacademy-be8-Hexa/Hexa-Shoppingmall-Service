package com.nhnacademy.hexashoppingmallservice.exception.cartException;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class CartAlreadyExistException extends ResourceConflictException {
    public CartAlreadyExistException(String message) {
        super(message);
    }
}
