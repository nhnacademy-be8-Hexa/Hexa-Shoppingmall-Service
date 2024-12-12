package com.nhnacademy.hexashoppingmallservice.advice;

import com.nhnacademy.hexashoppingmallservice.exception.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {
    @ExceptionHandler({
            MemberAlreadyExistException.class
    })
    public ResponseEntity<Void> handleMemberAlreadyExistException(MemberAlreadyExistException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({
            MemberNotFoundException.class
    })
    public ResponseEntity<Void> handleMemberNotFoundException(MemberNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({
            SqlQueryExecuteFailException.class
    })
    public ResponseEntity<Void> handleSqlQueryExecuteFailException(SqlQueryExecuteFailException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
