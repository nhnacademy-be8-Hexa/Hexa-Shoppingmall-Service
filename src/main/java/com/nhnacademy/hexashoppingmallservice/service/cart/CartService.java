package com.nhnacademy.hexashoppingmallservice.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private static final String CART_KEY_PREFIX = "cart:";

    private final RedisTemplate<String, String> redisTemplate;

    public String getCart(String memberId) {
        String key = CART_KEY_PREFIX + memberId;
        String res = redisTemplate.opsForValue().get(key);
        if (res == null) {
            return "[]";
        }
        return res;
    }

    public void setCart(String memberId, String cart) {
        String key = CART_KEY_PREFIX + memberId;
        redisTemplate.opsForValue().set(key, cart);
    }

}
