package com.nhnacademy.hexashoppingmallservice.repository.Cart;

import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
