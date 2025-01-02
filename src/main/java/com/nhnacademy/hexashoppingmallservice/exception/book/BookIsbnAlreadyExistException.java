package com.nhnacademy.hexashoppingmallservice.exception.book;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class BookIsbnAlreadyExistException extends ResourceConflictException {
    public BookIsbnAlreadyExistException(String message){
        super(message);
    }
}
