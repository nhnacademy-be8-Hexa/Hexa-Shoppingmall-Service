package com.nhnacademy.hexashoppingmallservice.actuator;

import org.springframework.stereotype.Component;

@Component
public final class ApplicationStatus {
    private boolean status = true;

    public void stopService(){
        this.status = false;
    }

    public void startService(){
        this.status = true;
    }

    public boolean getStatus(){
        return status;
    }
}
