package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.PointPolicy;
import com.nhnacademy.hexashoppingmallservice.service.order.PointPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pointPolicies")
public class PointPolicyController {
    private final PointPolicyService pointPolicyService;

    @PostMapping
    public ResponseEntity<PointPolicy> createPointPolicy(@RequestBody @Valid PointPolicy pointPolicy) {
        PointPolicy createdPointPolicy = pointPolicyService.createPointPolicy(pointPolicy);
        return new ResponseEntity<>(createdPointPolicy, HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<PointPolicy> updatePointPolicy(@RequestBody @Valid PointPolicy pointPolicy) {
        PointPolicy updatedPointPolicy = pointPolicyService.updatePointPolicy(pointPolicy);
        return ResponseEntity.ok(updatedPointPolicy);
    }

    @GetMapping
    public ResponseEntity<List<PointPolicy>> getAllPointPolicies() {
        List<PointPolicy> pointPolicies = pointPolicyService.getAllPointPolicies();
        return ResponseEntity.ok(pointPolicies);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePointPolicy(@RequestBody PointPolicy pointPolicy) {
        pointPolicyService.deletePointPolicy(pointPolicy);
        return ResponseEntity.noContent().build();
    }
}
