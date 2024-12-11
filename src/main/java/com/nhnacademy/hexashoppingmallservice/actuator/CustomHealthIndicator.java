package com.nhnacademy.hexashoppingmallservice.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {
    private final ApplicationStatus applicationStatus;

    public CustomHealthIndicator(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    @Override
    public Health health() {
        if(!applicationStatus.getStatus()){
            return Health.down().build();
        }
        return Health.up().withDetail("service","start").build();
    }
}
