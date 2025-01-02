package com.nhnacademy.hexashoppingmallservice.exception.member;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class MemberNotFoundException extends ResourceNotFoundException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
