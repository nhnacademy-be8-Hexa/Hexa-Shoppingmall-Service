package com.nhnacademy.hexashoppingmallservice.exception.address;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class AddressFullException extends ResourceConflictException {
    public AddressFullException(String message) {
        super(message);
    }
}
