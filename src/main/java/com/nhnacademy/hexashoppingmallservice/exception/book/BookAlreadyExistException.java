package com.nhnacademy.hexashoppingmallservice.exception.book;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;

public class BookAlreadyExistException extends ResourceConflictException {
    public BookAlreadyExistException(String message){
        super(message);
    }
}
