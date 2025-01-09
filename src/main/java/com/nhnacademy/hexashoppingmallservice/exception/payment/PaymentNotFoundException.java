package com.nhnacademy.hexashoppingmallservice.exception.payment;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class PaymentNotFoundException extends ResourceNotFoundException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
