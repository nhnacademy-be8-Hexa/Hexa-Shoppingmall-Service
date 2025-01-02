package com.nhnacademy.hexashoppingmallservice.exception.order;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class GuestOrderNotFoundException extends ResourceNotFoundException {
    public GuestOrderNotFoundException(String message) {
        super(message);
    }
}
