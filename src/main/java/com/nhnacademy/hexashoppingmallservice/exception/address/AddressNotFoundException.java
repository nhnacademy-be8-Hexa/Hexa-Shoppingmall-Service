package com.nhnacademy.hexashoppingmallservice.exception.address;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class AddressNotFoundException extends ResourceNotFoundException {
    public AddressNotFoundException(String message) {
        super(message);
    }
}
