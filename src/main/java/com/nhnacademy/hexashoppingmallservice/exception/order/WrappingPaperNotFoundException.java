package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class WrappingPaperNotFoundException extends ResourceNotFoundException {
    public WrappingPaperNotFoundException(String message) {
        super(message);
    }
}
