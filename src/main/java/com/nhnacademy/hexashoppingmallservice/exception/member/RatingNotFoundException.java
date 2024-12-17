package com.nhnacademy.hexashoppingmallservice.exception.member;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}
