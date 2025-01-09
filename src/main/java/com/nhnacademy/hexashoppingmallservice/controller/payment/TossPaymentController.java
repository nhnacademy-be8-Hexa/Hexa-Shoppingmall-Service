package com.nhnacademy.hexashoppingmallservice.controller.payment;

import com.nhnacademy.hexashoppingmallservice.entity.payment.TossPayment;
import com.nhnacademy.hexashoppingmallservice.service.payment.TossPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/toss-payment")
public class TossPaymentController {
    private final TossPaymentService tossPaymentService;

    @PostMapping
    public ResponseEntity<?> addPayment(
            @RequestBody TossPayment tossPayment
    ) {
        tossPaymentService.create(tossPayment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{paymentKey}")
    public ResponseEntity<TossPayment> getPayment(
            @PathVariable String paymentKey
    ){
        return ResponseEntity.ok(tossPaymentService.getPayment(paymentKey));
    }

}
