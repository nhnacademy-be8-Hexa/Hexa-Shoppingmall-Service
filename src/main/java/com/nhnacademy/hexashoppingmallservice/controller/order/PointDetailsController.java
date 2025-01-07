package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.CreatePointDetailDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.PointDetailsService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/pointDetails")
public class PointDetailsController {
    private final PointDetailsService pointDetailsService;
    private final JwtUtils jwtUtils;

    /**
     * 포인트 상세 정보 생성 엔드포인트
     *
     * @param memberId     포인트를 추가할 회원의 ID
     * @param pointDetails 생성할 포인트 상세 정보
     * @return 생성된 포인트 상세 정보
     */
    @PostMapping
    public ResponseEntity<PointDetails> createPointDetails(
            @PathVariable String memberId,
            @RequestBody @Valid CreatePointDetailDTO pointDetails) {
        PointDetails createdPointDetails = pointDetailsService.createPointDetails(pointDetails, memberId);
        return new ResponseEntity<>(createdPointDetails, HttpStatus.CREATED);
    }

    /**
     * 회원의 포인트 합계 조회 엔드포인트
     *
     * @param memberId 포인트 합계를 조회할 회원의 ID
     * @return 회원의 포인트 합계
     */
    @GetMapping("/sum")
    public ResponseEntity<Long> sumPoint(
            @PathVariable String memberId , HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        Long sum = pointDetailsService.sumPoint(memberId);
        if(sum == null) {
            sum = 0L;
        }
        return ResponseEntity.ok(sum);
    }

    /**
     * 회원의 포인트 상세 정보 목록 조회 엔드포인트
     *
     * @param memberId 포인트 상세 정보를 조회할 회원의 ID
     * @param pageable 페이징 정보
     * @return 포인트 상세 정보 목록
     */
    @GetMapping
    public ResponseEntity<List<PointDetailsProjection>> getPointDetails(
            @PathVariable String memberId,
            Pageable pageable , HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        List<PointDetailsProjection> pointDetails = pointDetailsService.getPointDetails(pageable, memberId);
        return ResponseEntity.ok(pointDetails);
    }
}
