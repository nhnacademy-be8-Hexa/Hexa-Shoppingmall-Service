package com.nhnacademy.hexashoppingmallservice.exception.book;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

public class PublisherNotFoundException extends ResourceNotFoundException {
    public PublisherNotFoundException(String message){
        super(message);
    }
}
