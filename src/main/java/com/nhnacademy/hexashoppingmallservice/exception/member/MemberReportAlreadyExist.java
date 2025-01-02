package com.nhnacademy.hexashoppingmallservice.exception.member;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class MemberReportAlreadyExist extends ResourceConflictException {
    public MemberReportAlreadyExist(String message) {
        super(message);
    }
}
