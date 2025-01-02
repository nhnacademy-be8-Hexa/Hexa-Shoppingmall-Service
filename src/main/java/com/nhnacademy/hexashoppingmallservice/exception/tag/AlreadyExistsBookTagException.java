package com.nhnacademy.hexashoppingmallservice.exception.tag;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class AlreadyExistsBookTagException extends ResourceConflictException {
    public AlreadyExistsBookTagException(String message) {
        super(message);
    }
}
