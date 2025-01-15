package com.nhnacademy.hexashoppingmallservice.dto.order;

import jakarta.validation.constraints.NotNull;

public record DeliveryCostPolicyRequest(
        @NotNull int deliveryCost,
        @NotNull int freeMinimumAmount,
        @NotNull String createdBy
) {
}
