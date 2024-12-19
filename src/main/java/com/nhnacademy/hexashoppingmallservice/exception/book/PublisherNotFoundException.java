package com.nhnacademy.hexashoppingmallservice.exception.book;

public class PublisherNotFoundException extends RuntimeException{
    public PublisherNotFoundException(String message){
        super(message);
    }
}
