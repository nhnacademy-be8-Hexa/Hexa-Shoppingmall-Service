package com.nhnacademy.hexashoppingmallservice.log;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

class LogCrashAppenderTest {

    @Mock
    private RestTemplate restTemplate;  // Mock RestTemplate

    @InjectMocks
    private LogCrashAppender logCrashAppender;  // Class under test

    private ILoggingEvent mockLoggingEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockLoggingEvent = Mockito.mock(ILoggingEvent.class);  // Mock ILoggingEvent
        logCrashAppender.setUrl("http://localhost:8080/crash-log");
    }


    @Test
    void testJsonProcessingExceptionHandling() {
        // Given
        String invalidUrl = null;  // Simulate invalid URL scenario
        logCrashAppender.setUrl(invalidUrl);

        // When & Then
        // Ensure that a RuntimeException is thrown due to invalid URL or JSON processing failure
        assertThrows(RuntimeException.class, () -> logCrashAppender.append(mockLoggingEvent));
    }

}
