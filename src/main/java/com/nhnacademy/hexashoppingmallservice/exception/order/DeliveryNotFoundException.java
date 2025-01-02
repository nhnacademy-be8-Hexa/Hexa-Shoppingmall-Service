package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class DeliveryNotFoundException extends ResourceNotFoundException {
    public DeliveryNotFoundException(String message) {
        super(message);
    }
}
