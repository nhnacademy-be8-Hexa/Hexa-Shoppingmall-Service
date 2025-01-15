package com.nhnacademy.hexashoppingmallservice.controller.cart;

import com.nhnacademy.hexashoppingmallservice.service.cart.CartService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final JwtUtils jwtUtils;

    @GetMapping("/api/members/{memberId}/carts")
    public ResponseEntity<String> getCart(
            @PathVariable String memberId,
            HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);

        String cartDtos = cartService.getCart(memberId);
        return ResponseEntity.ok(cartDtos);
    }

    @PutMapping("/api/members/{memberId}/carts")
    public ResponseEntity<String> setCart(
            @PathVariable String memberId,
            @RequestBody String cart,
            HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);

        cartService.setCart(memberId, cart);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니가 성공적으로 설정되었습니다.");
    }

}
