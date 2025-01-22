package com.nhnacademy.hexashoppingmallservice.exception.cartException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

class CartNotFoundExceptionTest {

    @Test
    void testCartNotFoundExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Cart not found!";

        // 예외 발생
        CartNotFoundException exception = new CartNotFoundException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCartNotFoundExceptionIsInstanceOfResourceNotFoundException() {
        // 예외 발생
        CartNotFoundException exception = new CartNotFoundException("Cart not found!");

        // 예외가 ResourceNotFoundException의 인스턴스인지 확인
        assertTrue(exception instanceof ResourceNotFoundException);
    }

    @Test
    void testCartNotFoundExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        CartNotFoundException exception = new CartNotFoundException("Cart not found!");

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
