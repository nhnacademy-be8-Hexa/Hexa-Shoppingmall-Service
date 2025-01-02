package com.nhnacademy.hexashoppingmallservice.exception.book;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String message){
        super(message);
    }
}
