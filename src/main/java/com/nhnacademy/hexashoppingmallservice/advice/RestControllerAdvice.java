package com.nhnacademy.hexashoppingmallservice.advice;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;

import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {
    @ExceptionHandler({
            ResourceConflictException.class,
    })
    public ResponseEntity<Void> handleAlreadyExistException(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({
            ResourceNotFoundException.class
    })
    public ResponseEntity<Void> handleNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({
            SqlQueryExecuteFailException.class
    })
    public ResponseEntity<Void> handleSqlQueryExecuteFailException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
