package com.nhnacademy.hexashoppingmallservice.exception.member;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class RatingNotFoundException extends ResourceNotFoundException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}
