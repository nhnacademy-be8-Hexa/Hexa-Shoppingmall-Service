package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.cart.CartRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.entity.order.Delivery;
import com.nhnacademy.hexashoppingmallservice.service.order.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/api/delivery")
    public ResponseEntity<Delivery> createDelivery(@RequestBody @Valid DeliveryRequestDTO deliveryRequestDTO) {
        return ResponseEntity.ok(deliveryService.createDelivery(deliveryRequestDTO));
    }

    @GetMapping("/api/delivery")
    public List<Delivery> getAllDelivery() {
        return deliveryService.getDeliveries();
    }

    @GetMapping("/api/delivery/{orderId}")
    public Delivery getDelivery(@PathVariable Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId);
    }

    @GetMapping("/api/delivery/{memberId}")
    public Delivery getDeliveryByMemberId(@PathVariable String memberId) {
        return deliveryService.getDeliveryByMemberId(memberId);
    }

    @PatchMapping("/api/delivery/{orderId}")
    public ResponseEntity<Delivery> updateDelivery(@PathVariable Long orderId, @RequestBody @Valid DeliveryRequestDTO deliveryRequestDTO)
    {
        Delivery delivery = deliveryService.updateDelivery(orderId, deliveryRequestDTO);
        return ResponseEntity.ok(delivery);
    }
}
