package com.nhnacademy.hexashoppingmallservice.exception.member;

public class MemberAlreadyExistException extends RuntimeException {
    public MemberAlreadyExistException(String message) {
        super(message);
    }
}
