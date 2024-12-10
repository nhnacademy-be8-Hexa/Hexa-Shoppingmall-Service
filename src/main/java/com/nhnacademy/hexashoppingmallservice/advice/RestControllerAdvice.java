package com.nhnacademy.hexashoppingmallservice.advice;

import com.nhnacademy.hexashoppingmallservice.exception.MemberAlreadyExistException;
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
}
