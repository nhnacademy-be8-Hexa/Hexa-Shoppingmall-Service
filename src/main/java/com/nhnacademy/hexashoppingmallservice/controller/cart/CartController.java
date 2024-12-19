package com.nhnacademy.hexashoppingmallservice.controller.cart;

import com.nhnacademy.hexashoppingmallservice.dto.cart.CartRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.repository.cart.CartRepository;
import com.nhnacademy.hexashoppingmallservice.service.cart.CartService;
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

    @GetMapping("/api/carts")
    public List<Cart> getCarts() {
        return cartService.getCarts();
    }

    @GetMapping("/api/carts/{cartId}")
    public Cart getCart(@PathVariable Long cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping("/api/carts")
    public ResponseEntity<Cart> createCart(@RequestBody @Valid CartRequestDTO cartRequestDTO) {
        return ResponseEntity.ok(cartService.createCart(cartRequestDTO));
    }

    @DeleteMapping("/api/carts/{cartId}")
    public void deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
    }

    @DeleteMapping("/api/carts/member/{memberId}")
    public ResponseEntity<Void> clearCartByMember(@PathVariable Long memberId) {
        cartService.clearCartByMember(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/api/carts/{cartId}/quantity")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable Long cartId,
            @RequestBody CartRequestDTO cartRequestDto) {
        Cart updatedCart = cartService.updateCartItemQuantity(cartId, cartRequestDto);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }


}
