package com.nhnacademy.hexashoppingmallservice.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TokenPermissionDeniedTest {

    @Test
    void testTokenPermissionDeniedMessage() {
        // 예외 발생
        TokenPermissionDenied exception = new TokenPermissionDenied();

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals("Token Permission Denied!", exception.getMessage());
    }

    @Test
    void testTokenPermissionDeniedIsInstanceOfRuntimeException() {
        // 예외 발생
        TokenPermissionDenied exception = new TokenPermissionDenied();

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
