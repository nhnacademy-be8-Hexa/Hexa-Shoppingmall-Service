package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.entity.order.PointPolicy;
import com.nhnacademy.hexashoppingmallservice.service.order.PointPolicyService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<PointPolicy> createPointPolicy(@RequestBody @Valid PointPolicy pointPolicy, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        PointPolicy createdPointPolicy = pointPolicyService.createPointPolicy(pointPolicy);
        return new ResponseEntity<>(createdPointPolicy, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<PointPolicy> updatePointPolicy(@RequestBody @Valid PointPolicy pointPolicy, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        PointPolicy updatedPointPolicy = pointPolicyService.updatePointPolicy(pointPolicy);
        return ResponseEntity.ok(updatedPointPolicy);
    }

    @GetMapping
    public ResponseEntity<List<PointPolicy>> getAllPointPolicies() {
        List<PointPolicy> pointPolicies = pointPolicyService.getAllPointPolicies();
        return ResponseEntity.ok(pointPolicies);
    }

    @DeleteMapping("/{pointPolicyName}")
    public ResponseEntity<Void> deletePointPolicy(@PathVariable String pointPolicyName, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        pointPolicyService.deletePointPolicy(pointPolicyName);
        return ResponseEntity.noContent().build();
    }
}
