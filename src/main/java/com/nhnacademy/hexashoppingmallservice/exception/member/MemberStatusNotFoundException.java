package com.nhnacademy.hexashoppingmallservice.exception.member;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class MemberStatusNotFoundException extends ResourceNotFoundException {
    public MemberStatusNotFoundException(String message) {
        super(message.concat(" is not found"));
    }
}
