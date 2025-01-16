package com.nhnacademy.hexashoppingmallservice.entity.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class DeliveryCostPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryCostPolicyId;

    @NotNull
    private int deliveryCost;

    @NotNull
    private int freeMinimumAmount;

    @NotNull
    private String createdBy;

    @NotNull
    private LocalDateTime createdAt;
}
