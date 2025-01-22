package com.nhnacademy.hexashoppingmallservice.exception.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParameterNotEnouthExceptionTest {

    @Test
    void testParameterNotEnouthExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Not enough parameters provided!";

        // 예외 발생
        ParameterNotEnouthException exception = new ParameterNotEnouthException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testParameterNotEnouthExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        ParameterNotEnouthException exception = new ParameterNotEnouthException("Test message");

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
