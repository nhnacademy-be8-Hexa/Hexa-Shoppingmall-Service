package com.nhnacademy.hexashoppingmallservice.log;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogCrashRequestTest {

    @Test
    void testLogCrashRequestConstructor() {
        // Given
        String body = "Sample log body";

        // When
        LogCrashRequest logCrashRequest = new LogCrashRequest(body);

        // Then
        // Verify that the body field is correctly set
        assertEquals(body, logCrashRequest.getBody(), "Body should be set correctly");

        // Verify that the default values are correctly set
        assertEquals("nMWnKdBvAFvUW8XL", logCrashRequest.getProjectName(), "Project name should have default value");
        assertEquals("1.0.0", logCrashRequest.getProjectVersion(), "Project version should have default value");
        assertEquals("v2", logCrashRequest.getLogVersion(), "Log version should have default value");
        assertEquals("Hexa-Shoppingmall", logCrashRequest.getLogSource(), "Log source should have default value");
        assertEquals("log", logCrashRequest.getLogType(), "Log type should have default value");
        assertEquals("Hexa", logCrashRequest.getHost(), "Host should have default value");
        assertEquals("ERROR", logCrashRequest.getLogLevel(), "Log level should have default value");
    }

    @Test
    void testSettersAndGetters() {
        // Given
        LogCrashRequest logCrashRequest = new LogCrashRequest("Initial body");

        // When
        logCrashRequest.setProjectName("Test Project");
        logCrashRequest.setProjectVersion("2.0.0");
        logCrashRequest.setLogVersion("v3");
        logCrashRequest.setLogSource("Test Source");
        logCrashRequest.setLogType("test");
        logCrashRequest.setHost("Test Host");
        logCrashRequest.setLogLevel("INFO");
        logCrashRequest.setErrorCodeType("404");
        logCrashRequest.setCategory("Error");
        logCrashRequest.setSendTime("2023-01-01T00:00:00");

        // Then
        // Verify that the setter methods work correctly
        assertEquals("Test Project", logCrashRequest.getProjectName());
        assertEquals("2.0.0", logCrashRequest.getProjectVersion());
        assertEquals("v3", logCrashRequest.getLogVersion());
        assertEquals("Test Source", logCrashRequest.getLogSource());
        assertEquals("test", logCrashRequest.getLogType());
        assertEquals("Test Host", logCrashRequest.getHost());
        assertEquals("INFO", logCrashRequest.getLogLevel());
        assertEquals("404", logCrashRequest.getErrorCodeType());
        assertEquals("Error", logCrashRequest.getCategory());
        assertEquals("2023-01-01T00:00:00", logCrashRequest.getSendTime());
    }
}
