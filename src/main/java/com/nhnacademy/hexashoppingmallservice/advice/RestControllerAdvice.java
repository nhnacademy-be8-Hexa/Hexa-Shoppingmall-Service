package com.nhnacademy.hexashoppingmallservice.advice;

import com.nhnacademy.hexashoppingmallservice.exception.member.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {
    @ExceptionHandler({
            MemberAlreadyExistException.class
    })
    public ResponseEntity<Void> handleMemberAlreadyExistException(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({
            MemberNotFoundException.class,
            MemberStatusNotFoundException.class,
            RatingNotFoundException.class,
            OrderStatusNotFoundException.class
    })
    public ResponseEntity<Void> handleMemberNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler({
            SqlQueryExecuteFailException.class
    })
    public ResponseEntity<Void> handleSqlQueryExecuteFailException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
