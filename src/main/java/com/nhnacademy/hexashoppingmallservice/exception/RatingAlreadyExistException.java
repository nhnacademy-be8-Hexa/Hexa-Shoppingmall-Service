package com.nhnacademy.hexashoppingmallservice.exception;

public class RatingAlreadyExistException extends RuntimeException {
    public RatingAlreadyExistException(String message) {
        super(message);
    }
}
