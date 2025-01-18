package com.nhnacademy.hexashoppingmallservice.actuator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomHealthIndicatorTest {
    private ApplicationStatus applicationStatus;
    private CustomHealthIndicator customHealthIndicator;

    @BeforeEach
    void setUp() {
        applicationStatus = mock(ApplicationStatus.class);
        customHealthIndicator = new CustomHealthIndicator(applicationStatus);
    }

    @Test
    void health_whenApplicationStatusIsDown_shouldReturnHealthDown() {
        // Given
        when(applicationStatus.getStatus()).thenReturn(false);

        // When
        Health health = customHealthIndicator.health();

        // Then
        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
    }

    @Test
    void health_whenApplicationStatusIsUp_shouldReturnHealthUp() {
        // Given
        when(applicationStatus.getStatus()).thenReturn(true);

        // When
        Health health = customHealthIndicator.health();

        // Then
        assertThat(health.getStatus().getCode()).isEqualTo("UP");
        assertThat(health.getDetails()).containsEntry("service", "start");
    }
}
