package com.nhnacademy.hexashoppingmallservice.exception.point;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class PointPolicyAlreadyExistException extends ResourceConflictException {
    public PointPolicyAlreadyExistException(String message) {
        super(message);
    }
}
