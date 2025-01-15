package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryCostPolicyRequest;
import com.nhnacademy.hexashoppingmallservice.entity.order.DeliveryCostPolicy;
import com.nhnacademy.hexashoppingmallservice.exception.ResourceConflictException;
import com.nhnacademy.hexashoppingmallservice.exception.ResourceNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.DeliveryCostPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryCostPolicyService {
    private final DeliveryCostPolicyRepository deliveryCostPolicyRepository;

    public List<DeliveryCostPolicy> getAllPaging(
            Pageable pageable
    ) {
        return deliveryCostPolicyRepository.findAll(pageable).getContent();
    }

    public DeliveryCostPolicy getRecent() {
        DeliveryCostPolicy res = deliveryCostPolicyRepository.findFirstByOrderByDeliveryCostPolicyIdDesc();
        if(res == null) {
            throw new ResourceNotFoundException("배송비 정책이 없습니다.");
        }
        return res;
    }

    @Transactional
    public void create(DeliveryCostPolicyRequest request){
        if(deliveryCostPolicyRepository.existsByDeliveryCostAndFreeMinimumAmount(request.deliveryCost(), request.freeMinimumAmount())){
            throw new ResourceConflictException("동일한 배송비 정책이 이미 존재합니다.");
        }

        DeliveryCostPolicy deliveryCostPolicy = DeliveryCostPolicy.builder()
                .deliveryCost(request.deliveryCost())
                .freeMinimumAmount(request.freeMinimumAmount())
                .createdBy(request.createdBy())
                .createdAt(LocalDateTime.now())
                .build();

        deliveryCostPolicyRepository.save(deliveryCostPolicy);
    }

}
