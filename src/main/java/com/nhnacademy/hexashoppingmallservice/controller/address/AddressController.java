package com.nhnacademy.hexashoppingmallservice.controller.address;

import com.nhnacademy.hexashoppingmallservice.dto.address.AddressRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.address.Address;
import com.nhnacademy.hexashoppingmallservice.projection.address.AddressProjection;
import com.nhnacademy.hexashoppingmallservice.service.address.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class AddressController {
    private final AddressService addressService;

    /**
     * 새로운 주소를 추가하는 엔드포인트
     *
     * @param memberId 주소를 추가할 회원의 ID
     * @param addressRequestDTO  추가할 주소 정보
     * @return 생성된 주소 정보
     */
    @PostMapping("/{memberId}/addresses")
    public ResponseEntity<AddressRequestDTO> addAddress(
            @PathVariable String memberId,
            @RequestBody @Valid AddressRequestDTO addressRequestDTO) {

        // 서비스 호출하여 주소 추가
        addressService.addAddress(addressRequestDTO, memberId);

        return new ResponseEntity<>(addressRequestDTO, HttpStatus.CREATED);
    }

    /**
     * 특정 회원의 주소 목록을 조회하는 엔드포인트
     *
     * @param memberId 주소를 조회할 회원의 ID
     * @param pageable 페이징 정보
     * @return 주소 목록
     */
    @GetMapping("/{memberId}/addresses")
    public ResponseEntity<List<AddressProjection>> getAddresses(
            @PathVariable String memberId,
            Pageable pageable) {

        // 서비스 호출하여 주소 목록 조회
        List<AddressProjection> addresses = addressService.getAddress(pageable, memberId);

        return ResponseEntity.ok(addresses);
    }

    /**
     * 특정 주소를 삭제하는 엔드포인트
     *
     * @param addressId 삭제할 주소의 ID
     * @return 응답 없음
     */
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId) {

        // 서비스 호출하여 주소 삭제
        addressService.deleteAddress(addressId);

        return ResponseEntity.noContent().build();
    }
}