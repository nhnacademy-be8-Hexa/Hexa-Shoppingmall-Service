package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class OrderBookNotFoundException extends ResourceNotFoundException {
    public OrderBookNotFoundException(String message) {
        super(String.valueOf(message));
    }
}