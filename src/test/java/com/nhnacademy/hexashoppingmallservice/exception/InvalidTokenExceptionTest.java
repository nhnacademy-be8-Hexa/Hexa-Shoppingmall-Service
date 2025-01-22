package com.nhnacademy.hexashoppingmallservice.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InvalidTokenExceptionTest {

    @Test
    void testInvalidTokenExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Invalid token provided!";

        // 예외 발생
        InvalidTokenException exception = new InvalidTokenException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testInvalidTokenExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        InvalidTokenException exception = new InvalidTokenException("Invalid token");

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
