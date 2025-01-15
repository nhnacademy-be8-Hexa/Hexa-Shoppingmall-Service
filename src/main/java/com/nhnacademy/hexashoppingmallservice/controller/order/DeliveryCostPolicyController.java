package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryCostPolicyRequest;
import com.nhnacademy.hexashoppingmallservice.entity.order.DeliveryCostPolicy;
import com.nhnacademy.hexashoppingmallservice.service.order.DeliveryCostPolicyService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeliveryCostPolicyController {
    private final DeliveryCostPolicyService deliveryCostPolicyService;
    private final JwtUtils jwtUtils;

    @GetMapping("/api/delivery-cost-policy/recent")
    public ResponseEntity<DeliveryCostPolicy> getRecent(){
        DeliveryCostPolicy recent = deliveryCostPolicyService.getRecent();
        return ResponseEntity.ok(recent);
    }

    @GetMapping("/api/delivery-cost-policy/all")
    public ResponseEntity<List<DeliveryCostPolicy>> getAll(
            Pageable pageable,
            HttpServletRequest request
    ) {
        jwtUtils.ensureAdmin(request);

        List<DeliveryCostPolicy> list = deliveryCostPolicyService.getAllPaging(pageable);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/api/delivery-cost-policy/count")
    public ResponseEntity<Long> getCount(){
        Long count = deliveryCostPolicyService.countAll();
        return ResponseEntity.ok(count);
    }


    @PostMapping("/api/delivery-cost-policy")
    public ResponseEntity<?> create(@RequestBody DeliveryCostPolicyRequest deliveryCostPolicyRequest,
                                    HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);

        deliveryCostPolicyService.create(deliveryCostPolicyRequest);
        return ResponseEntity.ok().build();
    }
}
