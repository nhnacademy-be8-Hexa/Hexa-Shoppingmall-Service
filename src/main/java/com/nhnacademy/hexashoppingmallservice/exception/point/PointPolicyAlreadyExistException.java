package com.nhnacademy.hexashoppingmallservice.exception.point;

public class PointPolicyAlreadyExistException extends RuntimeException {
    public PointPolicyAlreadyExistException(String message) {
        super(message);
    }
}
