package com.nhnacademy.hexashoppingmallservice.exception.address;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String message) {
        super(message);
    }
}
