package com.nhnacademy.hexashoppingmallservice.exception.cartException;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class CartNotFoundException extends ResourceNotFoundException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
