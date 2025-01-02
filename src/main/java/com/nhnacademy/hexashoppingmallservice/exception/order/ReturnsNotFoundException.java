package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class ReturnsNotFoundException extends ResourceNotFoundException {
    public ReturnsNotFoundException(String message) {
        super(message);
    }
}
