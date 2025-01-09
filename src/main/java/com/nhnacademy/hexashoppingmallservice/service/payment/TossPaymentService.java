package com.nhnacademy.hexashoppingmallservice.service.payment;

import com.nhnacademy.hexashoppingmallservice.entity.payment.TossPayment;
import com.nhnacademy.hexashoppingmallservice.exception.payment.PaymentNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.payment.TossPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossPaymentService {
    private final TossPaymentRepository tossPaymentRepository;

    @Transactional
    public void create(
            TossPayment tossPayment
    ) {
        tossPaymentRepository.save(tossPayment);
    }

    public TossPayment getPayment(Long orderId) {
        return tossPaymentRepository.findById(orderId).orElseThrow(
                () -> new PaymentNotFoundException("payment not found : " + orderId)
        );
    }

}
