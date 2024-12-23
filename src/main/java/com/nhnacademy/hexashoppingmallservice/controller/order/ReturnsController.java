package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.Returns;
import com.nhnacademy.hexashoppingmallservice.service.order.ReturnsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReturnsController {
    private final ReturnsService returnsService;

    @PostMapping("/api/returns")
    public ResponseEntity<Returns> createReturns(@Valid @RequestBody ReturnsRequestDTO returnsRequestDTO) {
        return ResponseEntity.ok(returnsService.createReturns(returnsRequestDTO));
    }

    @GetMapping("/api/returns")
    public List<Returns> getAllReturns(Pageable pageable) {
        return returnsService.getReturns(pageable);
    }

    @GetMapping("/api/returns/returnReason/{ReturnReasonId}")
    public Returns getReturnsByReturnsReasonId(@PathVariable Long ReturnReasonId) {
        return returnsService.getReturnsByReturnsReasonId(ReturnReasonId);
    }

    @GetMapping("/api/returns/order/{orderId}")
    public Returns getReturnsByOrderId(@PathVariable Long orderId) {
        return returnsService.getReturnsByOrderId(orderId);
    }

    @GetMapping("/api/returns/member/{memberId}")
    public Returns getReturnsByMemberId(@PathVariable String memberId) {
        return returnsService.getReturnsByMemberId(memberId);
    }

    @PatchMapping("/api/returns/order/{orderId}")
    public ResponseEntity<Returns> updateReturns(@PathVariable Long orderId, @Valid @RequestBody ReturnsRequestDTO returnsRequestDTO) {
        return ResponseEntity.ok(returnsService.updateReturns(orderId, returnsRequestDTO));
    }

    @DeleteMapping("/api/returns/order/{orderId}")
    public void deleteReturns(@PathVariable Long orderId) {
        returnsService.deleteReturns(orderId);
    }
}
