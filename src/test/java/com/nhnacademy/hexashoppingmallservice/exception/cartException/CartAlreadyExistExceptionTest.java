package com.nhnacademy.hexashoppingmallservice.exception.cartException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import org.junit.jupiter.api.Test;

class CartAlreadyExistExceptionTest {

    @Test
    void testCartAlreadyExistExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Cart already exists!";

        // 예외 발생
        CartAlreadyExistException exception = new CartAlreadyExistException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCartAlreadyExistExceptionIsInstanceOfResourceConflictException() {
        // 예외 발생
        CartAlreadyExistException exception = new CartAlreadyExistException("Cart already exists!");

        // 예외가 ResourceConflictException의 인스턴스인지 확인
        assertTrue(exception instanceof ResourceConflictException);
    }

    @Test
    void testCartAlreadyExistExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        CartAlreadyExistException exception = new CartAlreadyExistException("Cart already exists!");

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
