package com.nhnacademy.hexashoppingmallservice.projection.order;

import java.time.LocalDateTime;

public interface PointDetailsProjection {
    Long getPointDetailsId();
    Integer getPointDetailsIncrement();
    String getPointDetailsComment();
    LocalDateTime getPointDetailsDatetime();
}
