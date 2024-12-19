package com.nhnacademy.hexashoppingmallservice.exception.book;

public class BookStatusNotFoundException extends RuntimeException{
    public BookStatusNotFoundException(String message){
        super(message);
    }
}
