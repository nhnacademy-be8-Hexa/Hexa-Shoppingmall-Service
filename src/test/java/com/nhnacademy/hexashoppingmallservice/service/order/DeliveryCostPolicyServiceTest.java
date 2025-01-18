package com.nhnacademy.hexashoppingmallservice.service.order;


import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryCostPolicyRequest;
import com.nhnacademy.hexashoppingmallservice.entity.order.DeliveryCostPolicy;
import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.DeliveryCostPolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DeliveryCostPolicyServiceTest {

    @InjectMocks
    private DeliveryCostPolicyService deliveryCostPolicyService;

    @Mock
    private DeliveryCostPolicyRepository deliveryCostPolicyRepository;

    @Test
    @DisplayName("배송비 정책 전체 조회 테스트 (페이지네이션 포함)")
    void getAllPagingTest() {
        // Given
        Pageable pageable = Pageable.unpaged();
        DeliveryCostPolicy policy =
                DeliveryCostPolicy.builder()
                        .deliveryCostPolicyId(1L)
                        .deliveryCost(1000)
                        .freeMinimumAmount(50000)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now()).build();
        List<DeliveryCostPolicy> policies = List.of(policy);
        PageImpl<DeliveryCostPolicy> page = new PageImpl<>(policies);

        when(deliveryCostPolicyRepository.findAll(pageable)).thenReturn(page);

        // When
        List<DeliveryCostPolicy> result = deliveryCostPolicyService.getAllPaging(pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getDeliveryCost()).isEqualTo(1000);
        verify(deliveryCostPolicyRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("배송비 정책 전체 개수 조회 테스트")
    void countAllTest() {
        // Given
        when(deliveryCostPolicyRepository.count()).thenReturn(10L);

        // When
        long count = deliveryCostPolicyService.countAll();

        // Then
        assertThat(count).isEqualTo(10L);
        verify(deliveryCostPolicyRepository, times(1)).count();
    }

    @Test
    @DisplayName("최근 배송비 정책 조회 테스트")
    void getRecentTest() {
        // Given
        DeliveryCostPolicy policy =
                DeliveryCostPolicy.builder()
                        .deliveryCostPolicyId(1L)
                        .deliveryCost(1000)
                        .freeMinimumAmount(50000)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now()).build();
        when(deliveryCostPolicyRepository.findFirstByOrderByDeliveryCostPolicyIdDesc()).thenReturn(policy);

        // When
        DeliveryCostPolicy result = deliveryCostPolicyService.getRecent();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryCost()).isEqualTo(1000);
        verify(deliveryCostPolicyRepository, times(1)).findFirstByOrderByDeliveryCostPolicyIdDesc();
    }

    @Test
    @DisplayName("최근 배송비 정책 조회 실패 테스트 - ResourceNotFoundException")
    void getRecentThrowsExceptionTest() {
        // Given
        when(deliveryCostPolicyRepository.findFirstByOrderByDeliveryCostPolicyIdDesc()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> deliveryCostPolicyService.getRecent())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("배송비 정책이 없습니다.");
        verify(deliveryCostPolicyRepository, times(1)).findFirstByOrderByDeliveryCostPolicyIdDesc();
    }

    @Test
    @DisplayName("배송비 정책 생성 테스트")
    void createTest() {
        // Given
        DeliveryCostPolicyRequest request = new DeliveryCostPolicyRequest(3000, 50000, "admin");
        DeliveryCostPolicy deliveryCostPolicy =
                DeliveryCostPolicy.builder()
                        .deliveryCostPolicyId(1L)
                        .deliveryCost(1000)
                        .freeMinimumAmount(50000)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now()).build();
        when(deliveryCostPolicyRepository.existsByDeliveryCostAndFreeMinimumAmount(request.deliveryCost(), request.freeMinimumAmount()))
                .thenReturn(false); // 동일한 정책이 없다면 생성 가능

        when(deliveryCostPolicyRepository.save(any(DeliveryCostPolicy.class))).thenReturn(deliveryCostPolicy);

        // When
        deliveryCostPolicyService.create(request);

        // Then
        verify(deliveryCostPolicyRepository, times(1)).existsByDeliveryCostAndFreeMinimumAmount(request.deliveryCost(), request.freeMinimumAmount());
        verify(deliveryCostPolicyRepository, times(1)).save(any(DeliveryCostPolicy.class));
    }

    @Test
    @DisplayName("동일한 배송비 정책이 있을 때 생성 실패 테스트 - ResourceConflictException")
    void createThrowsConflictExceptionTest() {
        // Given
        DeliveryCostPolicyRequest request = new DeliveryCostPolicyRequest(3000, 50000, "admin");

        // 동일한 정책이 이미 존재한다면 예외를 던짐
        when(deliveryCostPolicyRepository.existsByDeliveryCostAndFreeMinimumAmount(request.deliveryCost(), request.freeMinimumAmount()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> deliveryCostPolicyService.create(request))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("동일한 배송비 정책이 이미 존재합니다.");

        verify(deliveryCostPolicyRepository, times(1)).existsByDeliveryCostAndFreeMinimumAmount(request.deliveryCost(), request.freeMinimumAmount());
        verify(deliveryCostPolicyRepository, times(0)).save(any(DeliveryCostPolicy.class));
    }
}
