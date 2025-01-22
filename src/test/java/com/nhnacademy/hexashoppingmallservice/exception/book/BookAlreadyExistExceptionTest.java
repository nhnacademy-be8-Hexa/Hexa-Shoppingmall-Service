package com.nhnacademy.hexashoppingmallservice.exception.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import org.junit.jupiter.api.Test;

class BookAlreadyExistExceptionTest {

    @Test
    void testBookAlreadyExistExceptionMessage() {
        // Given
        String expectedMessage = "Book already exists in the system!";

        // When
        BookAlreadyExistException exception = new BookAlreadyExistException(expectedMessage);

        // Then
        assertEquals(expectedMessage, exception.getMessage(),
                "Exception message should be correctly passed to the constructor");
    }

    @Test
    void testBookAlreadyExistExceptionIsInstanceOfResourceConflictException() {
        // Given
        BookAlreadyExistException exception = new BookAlreadyExistException("Book already exists!");

        // When & Then
        assertTrue(exception instanceof ResourceConflictException,
                "Exception should be an instance of ResourceConflictException");
    }

    @Test
    void testBookAlreadyExistExceptionIsInstanceOfRuntimeException() {
        // Given
        BookAlreadyExistException exception = new BookAlreadyExistException("Book already exists!");

        // When & Then
        assertTrue(exception instanceof RuntimeException, "Exception should be an instance of RuntimeException");
    }
}
