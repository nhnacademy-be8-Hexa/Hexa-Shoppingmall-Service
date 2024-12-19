package com.nhnacademy.hexashoppingmallservice.exception.book;

public class BookIsbnAlreadyExistException extends RuntimeException{
    public BookIsbnAlreadyExistException(String message){
        super(message);
    }
}
