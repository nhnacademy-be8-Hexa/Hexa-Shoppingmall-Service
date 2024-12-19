package com.nhnacademy.hexashoppingmallservice.exception.book;

public class BookAlreadyExistException extends RuntimeException{
    public BookAlreadyExistException(String message){
        super(message);
    }
}
