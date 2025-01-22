package com.nhnacademy.hexashoppingmallservice.exception.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import org.junit.jupiter.api.Test;

class MemberAlreadyExistExceptionTest {

    @Test
    void testMemberAlreadyExistExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Member already exists!";

        // 예외 발생
        MemberAlreadyExistException exception = new MemberAlreadyExistException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testMemberAlreadyExistExceptionIsInstanceOfResourceConflictException() {
        // 예외 발생
        MemberAlreadyExistException exception = new MemberAlreadyExistException("Test message");

        // 예외가 ResourceConflictException의 인스턴스인지 확인
        assertTrue(exception instanceof ResourceConflictException);
    }

    @Test
    void testMemberAlreadyExistExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        MemberAlreadyExistException exception = new MemberAlreadyExistException("Test message");

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
