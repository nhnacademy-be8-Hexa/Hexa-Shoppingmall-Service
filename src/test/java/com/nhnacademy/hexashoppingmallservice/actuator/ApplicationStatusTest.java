package com.nhnacademy.hexashoppingmallservice.actuator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ApplicationStatusTest {

    private ApplicationStatus applicationStatus;

    @BeforeEach
    public void setUp() {
        // 각 테스트 전에 새로운 인스턴스를 생성
        applicationStatus = new ApplicationStatus();
    }

    @Test
    public void testInitialStatusIsTrue() {
        // 객체가 생성될 때 상태가 true인지 확인
        assertTrue(applicationStatus.getStatus(), "Initial status should be true");
    }

    @Test
    public void testStopServiceChangesStatusToFalse() {
        // 서비스 중지 후 상태가 false인지 확인
        applicationStatus.stopService();
        assertFalse(applicationStatus.getStatus(), "Status should be false after stopping the service");
    }

    @Test
    public void testStartServiceChangesStatusToTrue() {
        // 서비스 시작 후 상태가 true인지 확인
        applicationStatus.stopService();  // 먼저 서비스를 중지하고
        applicationStatus.startService(); // 그 후 서비스를 시작
        assertTrue(applicationStatus.getStatus(), "Status should be true after starting the service");
    }

    @Test
    public void testStopAndStartService() {
        // 서비스를 중지하고 시작한 후 상태를 확인
        applicationStatus.stopService();
        assertFalse(applicationStatus.getStatus(), "Status should be false after stopping the service");

        applicationStatus.startService();
        assertTrue(applicationStatus.getStatus(), "Status should be true after starting the service");
    }
}
