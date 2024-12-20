package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestDTO {
    private Long orderId;
    private Integer deliveryAmount;
    private LocalDateTime deliveryDate;
    private LocalDateTime deliveryReleaseDate;
}
