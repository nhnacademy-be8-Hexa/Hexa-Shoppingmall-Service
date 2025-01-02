package com.nhnacademy.hexashoppingmallservice.exception.book;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class BookStatusNotFoundException extends ResourceNotFoundException {
    public BookStatusNotFoundException(String message){
        super(message);
    }
}
