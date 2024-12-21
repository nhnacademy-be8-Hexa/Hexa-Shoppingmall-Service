package com.nhnacademy.hexashoppingmallservice.advice;

import com.nhnacademy.hexashoppingmallservice.exception.cartException.CartAlreadyExistException;

import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.exception.cartException.CartNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.RatingNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.DeliveryNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.OrderStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.order.ReturnsReasonNotFoundException;
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

    @ExceptionHandler({
            CartAlreadyExistException.class
    })
    public ResponseEntity<Void> handleCartAlreadyExistException(CartAlreadyExistException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    @ExceptionHandler({
            CartNotFoundException.class
    })
    public ResponseEntity<Void> handleCartNotFoundException(CartNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @ExceptionHandler({
            DeliveryNotFoundException.class
    })
    public ResponseEntity<Void> handleDeliveryNotFoundException(DeliveryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @ExceptionHandler({
            ReturnsReasonNotFoundException.class
    })
    public ResponseEntity<Void> ReturnsReasonNotFoundException(ReturnsReasonNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
