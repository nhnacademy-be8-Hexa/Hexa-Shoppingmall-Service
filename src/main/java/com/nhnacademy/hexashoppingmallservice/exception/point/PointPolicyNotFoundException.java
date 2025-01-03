package com.nhnacademy.hexashoppingmallservice.exception.point;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class PointPolicyNotFoundException extends ResourceNotFoundException {
    public PointPolicyNotFoundException(String message) {
        super(message);
    }
}
