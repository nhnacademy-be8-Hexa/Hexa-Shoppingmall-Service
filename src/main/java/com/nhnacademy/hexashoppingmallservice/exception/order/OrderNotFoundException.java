package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String message) {
        super(String.valueOf(message));
    }
}
