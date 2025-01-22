package com.nhnacademy.hexashoppingmallservice.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TokenNotFoundExceptionTest {

    @Test
    void testTokenNotFoundExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Token not found!";

        // 예외 발생
        TokenNotFoundException exception = new TokenNotFoundException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testTokenNotFoundExceptionIsInstanceOfResourceNotFoundException() {
        // 예외 발생
        TokenNotFoundException exception = new TokenNotFoundException("Token not found");

        // 예외가 ResourceNotFoundException의 인스턴스인지 확인
        assertTrue(exception instanceof ResourceNotFoundException);
    }

    @Test
    void testTokenNotFoundExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        TokenNotFoundException exception = new TokenNotFoundException("Token not found");

        // 예외가 RuntimeException의 인스턴스인지 확인 (간접적으로 상속 관계 검증)
        assertTrue(exception instanceof RuntimeException);
    }
}
