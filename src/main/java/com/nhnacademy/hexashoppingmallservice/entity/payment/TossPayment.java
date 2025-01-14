package com.nhnacademy.hexashoppingmallservice.entity.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TossPayment {
    @Id
    private Long orderId;

    @NotNull
    @Length(max = 50)
    private String paymentKey;

    @NotNull
    private int amount;
}
