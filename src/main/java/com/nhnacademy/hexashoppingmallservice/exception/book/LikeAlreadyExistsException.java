package com.nhnacademy.hexashoppingmallservice.exception.book;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException(String message) {
        super(message);
    }
}
