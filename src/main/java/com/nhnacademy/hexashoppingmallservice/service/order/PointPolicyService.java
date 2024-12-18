package com.nhnacademy.hexashoppingmallservice.service.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.PointPolicy;
import com.nhnacademy.hexashoppingmallservice.exception.point.PointPolicyAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.point.PointPolicyNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.order.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointPolicyService {
    private final PointPolicyRepository pointPolicyRepository;
    @Transactional(readOnly = true)
    public List<PointPolicy> getAllPointPolicies() {
        return pointPolicyRepository.findAll();
    }

    @Transactional
    public PointPolicy createPointPolicy(PointPolicy pointPolicy) {
        return pointPolicyRepository.save(pointPolicy);
    }

    @Transactional
    public PointPolicy updatePointPolicy(PointPolicy pointPolicy) {
        if (!pointPolicyRepository.existsById(pointPolicy.getPointPolicyName())) {
            throw new PointPolicyNotFoundException("Point policy %s is not found".formatted(pointPolicy.getPointPolicyName()));
        }

        PointPolicy getPointPolicy = pointPolicyRepository.findById(pointPolicy.getPointPolicyName()).get();
        getPointPolicy.setPointDelta(pointPolicy.getPointDelta());
        return getPointPolicy;
    }

    @Transactional
    public void deletePointPolicy(PointPolicy pointPolicy) {
        if (!pointPolicyRepository.existsById(pointPolicy.getPointPolicyName())) {
            throw new PointPolicyAlreadyExistException("Point policy %s is not found".formatted(pointPolicy.getPointPolicyName()));
        }
        pointPolicyRepository.deleteById(pointPolicy.getPointPolicyName());
    }
}
