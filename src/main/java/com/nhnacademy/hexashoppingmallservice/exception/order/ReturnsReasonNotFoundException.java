package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class ReturnsReasonNotFoundException extends ResourceNotFoundException {
    public ReturnsReasonNotFoundException(String message) {
        super(message);
    }
}
