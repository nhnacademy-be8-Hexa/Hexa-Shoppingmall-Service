package com.nhnacademy.hexashoppingmallservice.exception.book;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException(String message){
        super(message);
    }
}
