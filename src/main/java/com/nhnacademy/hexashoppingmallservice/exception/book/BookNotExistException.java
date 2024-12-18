package com.nhnacademy.hexashoppingmallservice.exception.book;

public class BookNotExistException extends RuntimeException {
    public BookNotExistException(String message) {
        super(message);
    }
}
