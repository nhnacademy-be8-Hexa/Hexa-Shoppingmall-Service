package com.nhnacademy.hexashoppingmallservice.exception.credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KeyManagerExceptionTest {

    @Test
    void testKeyManagerExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Key management failed!";

        // 예외 발생
        KeyManagerException exception = new KeyManagerException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testKeyManagerExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        KeyManagerException exception = new KeyManagerException("Test message");

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
