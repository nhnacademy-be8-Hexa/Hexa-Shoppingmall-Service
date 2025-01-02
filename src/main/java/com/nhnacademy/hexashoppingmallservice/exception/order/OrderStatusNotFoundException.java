package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class OrderStatusNotFoundException extends ResourceNotFoundException {
    public OrderStatusNotFoundException(String message) {
        super(message);
    }
}
