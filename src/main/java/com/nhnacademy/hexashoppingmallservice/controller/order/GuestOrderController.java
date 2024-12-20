package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.GuestOrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.GuestOrder;
import com.nhnacademy.hexashoppingmallservice.projection.member.order.GuestOrderProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.GuestOrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<GuestOrder> getAllGuestOrders(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, SIZE);
        return guestOrderService.getGuestOrders(pageable);
    }

    @GetMapping("/{orderId}")
    public GuestOrderProjection getGuestOrder(@PathVariable Long orderId) {
        return guestOrderService.getGuestOrder(orderId);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<GuestOrder> updateGuestOrder(@PathVariable Long orderId,
                                                       @Valid @RequestBody GuestOrderRequestDTO guestOrderRequestDTOs) {
        return ResponseEntity.ok(guestOrderService.updateGuestOrder(orderId, guestOrderRequestDTOs));
    }

}


