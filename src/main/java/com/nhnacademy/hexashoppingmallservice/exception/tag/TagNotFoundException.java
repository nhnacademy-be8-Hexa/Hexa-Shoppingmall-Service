package com.nhnacademy.hexashoppingmallservice.exception.tag;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class TagNotFoundException extends ResourceNotFoundException {
    public TagNotFoundException(String message) {
        super(message);
    }
}
