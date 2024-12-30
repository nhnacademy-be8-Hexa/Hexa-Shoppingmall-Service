package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryRequestDTO;
import com.nhnacademy.hexashoppingmallservice.projection.order.DeliveryProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.DeliveryService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;
    private final JwtUtils jwtUtils;

    @PostMapping("/api/deliveries")
    public ResponseEntity<Void> createDelivery(@RequestBody @Valid DeliveryRequestDTO deliveryRequestDTO) {
        deliveryService.createDelivery(deliveryRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/deliveries")
    public List<DeliveryProjection> getAllDelivery(Pageable pageable, HttpServletRequest request) {
        jwtUtils.ensureAdmin(request);
        return deliveryService.getDeliveries(pageable);
    }

    @GetMapping("/api/orders/{orderId}/deliveries")
    public DeliveryProjection getDelivery(@PathVariable Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId);
    }

    @GetMapping("/api/members/{memberId}/deliveries")
    public List<DeliveryProjection> getDeliveryByMemberId(@PathVariable String memberId, Pageable pageable,
                                                          HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        return deliveryService.getDeliveriesByMemberId(memberId, pageable);
    }

    @PutMapping("/api/orders/{orderId}/deliveries")
    public ResponseEntity<Void> updateDelivery(@PathVariable Long orderId,
                                               @RequestBody @Valid DeliveryRequestDTO deliveryRequestDTO) {
        deliveryService.updateDelivery(orderId, deliveryRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
