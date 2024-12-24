package com.nhnacademy.hexashoppingmallservice.exception;

public class TokenPermissionDenied extends RuntimeException {
    public TokenPermissionDenied() {
        super("Token Permission Denied!");
    }
}
