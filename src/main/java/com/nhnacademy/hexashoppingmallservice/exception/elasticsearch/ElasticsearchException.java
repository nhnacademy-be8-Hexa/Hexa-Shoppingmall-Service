package com.nhnacademy.hexashoppingmallservice.exception.elasticsearch;

public class ElasticsearchException extends RuntimeException {
    public ElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
