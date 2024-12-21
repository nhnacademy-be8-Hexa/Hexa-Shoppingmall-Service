package com.nhnacademy.hexashoppingmallservice.controller.order;


import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsReasonRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.ReturnsReason;
import com.nhnacademy.hexashoppingmallservice.service.order.ReturnsReasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReturnsReasonController {
    private final ReturnsReasonService returnsReasonService;

    @PostMapping("/api/returnsReason")
    public ResponseEntity<ReturnsReason> createReturnsReason(@Valid @RequestBody ReturnsReasonRequestDTO returnsReasonRequestDTO) {
        return ResponseEntity.ok(returnsReasonService.createReturnsReason(returnsReasonRequestDTO));
    }

    @GetMapping("/api/returnsReason")
    public List<ReturnsReason> getAllReturnsReasons(Pageable pageable) {
        return returnsReasonService.getReturnsReasons(pageable);
    }

    @GetMapping("/api/returnsReason/{returnsReasonId}")
    public ReturnsReason getReturnsReasonById(@PathVariable Long returnsReasonId) {
        return returnsReasonService.getReturnsReason(returnsReasonId);

    }
//
//    @GetMapping("/api/returnsReason/member/{memberId}")
//    public ReturnsReason getReturnsReasonByMemberId(@PathVariable Long memberId) {
//        return returnsReasonService.getReturnsReason(memberId);
//    }

    @PatchMapping("/api/returnsReason/{returnsReasonId}")
    public ResponseEntity<ReturnsReason> updateReturnsReason(@PathVariable Long returnsReasonId, @Valid @RequestBody ReturnsReasonRequestDTO returnsReasonRequestDTO) {
        ReturnsReason returnsReason = returnsReasonService.updateReturnsReason(returnsReasonId, returnsReasonRequestDTO);
        return ResponseEntity.ok(returnsReason);
    }

    @DeleteMapping("/api/returnsReason/{returnsReasonId}")
    public void deleteReturnsReason(@PathVariable Long returnsReasonId) {
        returnsReasonService.deleteReturnsReason(returnsReasonId);
    }

}
