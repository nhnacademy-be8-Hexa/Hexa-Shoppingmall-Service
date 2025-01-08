package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.GuestOrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.order.GuestOrderValidateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.GuestOrder;
import com.nhnacademy.hexashoppingmallservice.projection.order.GuestOrderProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.GuestOrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/guestOrders")
public class GuestOrderController {
    private final Integer SIZE = 10;
    private final GuestOrderService guestOrderService;

    @PostMapping
    public ResponseEntity<GuestOrder> createGuestOrder(@Valid @RequestBody GuestOrderRequestDTO guestOrderRequestDTO) {
        return ResponseEntity.status(201).body(guestOrderService.createGuestOrder(guestOrderRequestDTO));
    }

    @GetMapping
    public List<GuestOrderProjection> getAllGuestOrders(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, SIZE);
        return guestOrderService.getGuestOrders(pageable);
    }

    @GetMapping("/{orderId}")
    public GuestOrderProjection getGuestOrder(@PathVariable Long orderId) {
        return guestOrderService.getGuestOrder(orderId);
    }

    @PutMapping
    public ResponseEntity<GuestOrder> updateGuestOrder(@Valid @RequestBody GuestOrderRequestDTO guestOrderRequestDTOs) {
        return ResponseEntity.ok(guestOrderService.updateGuestOrder(guestOrderRequestDTOs));
    }

    // orderId와 guestOrderPassword로 존재 여부 확인
    @PostMapping("/validate")
    public ResponseEntity<String> validateGuestOrder(@Valid @RequestBody GuestOrderValidateRequestDTO guestOrderValidateRequestDTO) {
        String password = guestOrderService.getGuestOrderPassword(guestOrderValidateRequestDTO.getOrderId(), guestOrderValidateRequestDTO.getGuestOrderPassword());
        return ResponseEntity.ok(password);
    }


}


