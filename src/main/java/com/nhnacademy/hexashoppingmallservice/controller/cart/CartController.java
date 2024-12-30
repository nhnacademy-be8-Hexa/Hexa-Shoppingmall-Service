package com.nhnacademy.hexashoppingmallservice.controller.cart;

import com.nhnacademy.hexashoppingmallservice.dto.cart.CartRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.projection.cart.CartProjection;
import com.nhnacademy.hexashoppingmallservice.repository.cart.CartRepository;
import com.nhnacademy.hexashoppingmallservice.service.cart.CartService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final JwtUtils jwtUtils;

    @GetMapping("/api/members/{memberId}/carts/{cartId}")
    public CartProjection getCart(@PathVariable String memberId, @PathVariable Long cartId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        return cartService.getCart(cartId);
    }

    @GetMapping("/api/members/{memberId}/carts")
    public List<CartProjection> getCartByMemberId(@PathVariable String memberId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        return cartService.getCartByMemberId(memberId);
    }

    @PostMapping("/api/carts")
    public ResponseEntity<Void> createCart(@RequestBody @Valid CartRequestDTO cartRequestDTO) {
        cartService.createCart(cartRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/members/{memberId}/carts/{cartId}")
    public void deleteCart(@PathVariable String memberId, @PathVariable Long cartId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        cartService.deleteCart(cartId);
    }

    @DeleteMapping("/api/members/{memberId}/carts")
    public ResponseEntity<Void> clearCartByMember(@PathVariable String memberId, HttpServletRequest request) {
        jwtUtils.ensureUserAccess(request, memberId);
        cartService.clearCartByMember(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/api/carts/{cartId}/quantity")
    public ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable Long cartId,
            @RequestBody CartRequestDTO cartRequestDto) {
        cartService.updateCartItemQuantity(cartId, cartRequestDto);
        return ResponseEntity.noContent().build();
    }
}
