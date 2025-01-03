package com.nhnacademy.hexashoppingmallservice.exception;

public class TokenNotFoundException extends ResourceNotFoundException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
