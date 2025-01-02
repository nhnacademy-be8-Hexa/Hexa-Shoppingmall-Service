package com.nhnacademy.hexashoppingmallservice.exception.category;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
