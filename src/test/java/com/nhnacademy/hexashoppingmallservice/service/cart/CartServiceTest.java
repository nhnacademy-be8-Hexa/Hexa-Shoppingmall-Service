package com.nhnacademy.hexashoppingmallservice.service.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CartService cartService;

    private final String memberId = "whskgys";
    private final String cartKey = "cart:" + memberId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("getCart - 기존 카트가 존재하는 경우")
    void testGetCartExists() {
        // given
        String expectedCart = "[{\"item\":\"book1\",\"quantity\":2},{\"item\":\"book2\",\"quantity\":1}]";
        when(valueOperations.get(cartKey)).thenReturn(expectedCart);

        // when
        String actualCart = cartService.getCart(memberId);

        // then
        assertEquals(expectedCart, actualCart, "Redis에서 가져온 카트 데이터가 일치해야 합니다.");
        verify(valueOperations, times(1)).get(cartKey);
    }

    @Test
    @DisplayName("getCart - 기존 카트가 존재하지 않는 경우")
    void testGetCartNotExists() {
        // given
        when(valueOperations.get(cartKey)).thenReturn(null);

        // when
        String actualCart = cartService.getCart(memberId);

        // then
        assertEquals("[]", actualCart, "카트가 존재하지 않을 경우 빈 배열 문자열을 반환해야 합니다.");
        verify(valueOperations, times(1)).get(cartKey);
    }

    @Test
    @DisplayName("setCart - 카트 데이터를 Redis에 저장하는 경우")
    void testSetCart() {
        // given
        String cartData = "[{\"item\":\"book3\",\"quantity\":1}]";

        // when
        cartService.setCart(memberId, cartData);

        // then
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, times(1)).set(keyCaptor.capture(), valueCaptor.capture());

        assertEquals(cartKey, keyCaptor.getValue(), "Redis에 저장될 키가 올바르게 설정되어야 합니다.");
        assertEquals(cartData, valueCaptor.getValue(), "Redis에 저장될 값이 올바르게 설정되어야 합니다.");
    }
}
