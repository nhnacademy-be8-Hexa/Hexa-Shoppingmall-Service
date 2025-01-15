package com.nhnacademy.hexashoppingmallservice.service.payment;

import com.nhnacademy.hexashoppingmallservice.entity.payment.TossPayment;
import com.nhnacademy.hexashoppingmallservice.exception.payment.PaymentNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.payment.TossPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)  // MockitoExtension을 추가해줍니다.
class TossPaymentServiceTest {

    @Mock
    private TossPaymentRepository tossPaymentRepository;

    @InjectMocks
    private TossPaymentService tossPaymentService;

    private TossPayment tossPayment;

    @BeforeEach
    void setUp() {
        // TossPayment 객체 생성 시 필요한 모든 필드를 채워줍니다.
        tossPayment = new TossPayment(1L, "paymentKey123", 1000);
    }

    @Test
    void testCreate() {
        // Given
        // TossPayment 객체가 저장되는 것만 확인할 수 있음

        // When
        tossPaymentService.create(tossPayment);

        // Then
        verify(tossPaymentRepository, times(1)).save(tossPayment); // save가 한 번 호출되었는지 확인
    }

    @Test
    void testGetPayment_success() {
        // Given
        Long orderId = tossPayment.getOrderId();
        when(tossPaymentRepository.findById(orderId)).thenReturn(java.util.Optional.of(tossPayment));

        // When
        TossPayment result = tossPaymentService.getPayment(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals("paymentKey123", result.getPaymentKey());
        assertEquals(1000, result.getAmount());
    }

    @Test
    void testGetPayment_notFound() {
        // Given
        Long orderId = tossPayment.getOrderId();
        when(tossPaymentRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        // When & Then
        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () -> {
            tossPaymentService.getPayment(orderId);
        });

        assertEquals("payment not found : " + orderId, exception.getMessage());
    }
}
