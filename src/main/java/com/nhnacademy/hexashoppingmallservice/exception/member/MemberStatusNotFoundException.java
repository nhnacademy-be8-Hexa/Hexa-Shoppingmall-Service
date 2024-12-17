package com.nhnacademy.hexashoppingmallservice.exception.member;

public class MemberStatusNotFoundException extends RuntimeException {
    public MemberStatusNotFoundException(String message) {
        super(message.concat(" is not found"));
    }
}
