package com.nhnacademy.hexashoppingmallservice.exception;

public class SqlQueryExecuteFailException extends RuntimeException {
    public SqlQueryExecuteFailException(String message) {
        super(message.concat(" is not executed successfully"));
    }
}
