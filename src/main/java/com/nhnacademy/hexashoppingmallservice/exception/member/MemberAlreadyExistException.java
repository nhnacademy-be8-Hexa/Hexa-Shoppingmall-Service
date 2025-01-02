package com.nhnacademy.hexashoppingmallservice.exception.member;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class MemberAlreadyExistException extends ResourceConflictException {
    public MemberAlreadyExistException(String message) {
        super(message);
    }
}
