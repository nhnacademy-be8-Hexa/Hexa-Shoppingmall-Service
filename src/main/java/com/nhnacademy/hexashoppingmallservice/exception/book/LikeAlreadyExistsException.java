package com.nhnacademy.hexashoppingmallservice.exception.book;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class LikeAlreadyExistsException extends ResourceConflictException {
    public LikeAlreadyExistsException(String message) {
        super(message);
    }
}
