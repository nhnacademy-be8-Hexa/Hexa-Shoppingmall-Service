package com.nhnacademy.hexashoppingmallservice.repository.cart;

import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.projection.cart.CartProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<CartProjection> findAllByMemberMemberId(String memberId);
    Optional<CartProjection> findByCartId(Long cartId);
}
