package com.nhnacademy.hexashoppingmallservice.exception.review;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class ReviewNotFoundException extends ResourceNotFoundException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
