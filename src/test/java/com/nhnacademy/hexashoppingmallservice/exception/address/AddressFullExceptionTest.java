package com.nhnacademy.hexashoppingmallservice.exception.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import org.junit.jupiter.api.Test;

class AddressFullExceptionTest {

    @Test
    void testAddressFullExceptionMessage() {
        // 예외 메시지
        String expectedMessage = "Address limit reached!";

        // 예외 발생
        AddressFullException exception = new AddressFullException(expectedMessage);

        // 예외 메시지가 예상한 메시지와 일치하는지 확인
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testAddressFullExceptionIsInstanceOfResourceConflictException() {
        // 예외 발생
        AddressFullException exception = new AddressFullException("Address limit reached!");

        // 예외가 ResourceConflictException의 인스턴스인지 확인
        assertTrue(exception instanceof ResourceConflictException);
    }

    @Test
    void testAddressFullExceptionIsInstanceOfRuntimeException() {
        // 예외 발생
        AddressFullException exception = new AddressFullException("Address limit reached!");

        // 예외가 RuntimeException의 인스턴스인지 확인
        assertTrue(exception instanceof RuntimeException);
    }
}
