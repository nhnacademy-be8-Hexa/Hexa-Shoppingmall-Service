package com.nhnacademy.hexashoppingmallservice.exception.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

class PublisherNotFoundExceptionTest {

    @Test
    void testPublisherNotFoundExceptionMessage() {
        // Given
        String expectedMessage = "Publisher not found!";

        // When
        PublisherNotFoundException exception = new PublisherNotFoundException(expectedMessage);

        // Then
        assertEquals(expectedMessage, exception.getMessage(),
                "Exception message should be correctly passed to the constructor");
    }

    @Test
    void testPublisherNotFoundExceptionIsInstanceOfResourceNotFoundException() {
        // Given
        PublisherNotFoundException exception = new PublisherNotFoundException("Publisher not found!");

        // When & Then
        assertTrue(exception instanceof ResourceNotFoundException,
                "Exception should be an instance of ResourceNotFoundException");
    }

    @Test
    void testPublisherNotFoundExceptionIsInstanceOfRuntimeException() {
        // Given
        PublisherNotFoundException exception = new PublisherNotFoundException("Publisher not found!");

        // When & Then
        assertTrue(exception instanceof RuntimeException, "Exception should be an instance of RuntimeException");
    }
}
