package com.nhnacademy.hexashoppingmallservice.repository.cart;

import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAllByMember(Member member);
}
