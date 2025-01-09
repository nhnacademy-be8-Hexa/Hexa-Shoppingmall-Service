package com.nhnacademy.hexashoppingmallservice.repository.payment;

import com.nhnacademy.hexashoppingmallservice.entity.payment.TossPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {
    
}
