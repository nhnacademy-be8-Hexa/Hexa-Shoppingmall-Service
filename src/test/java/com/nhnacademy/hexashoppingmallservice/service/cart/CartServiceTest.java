package com.nhnacademy.hexashoppingmallservice.service.cart;

import com.nhnacademy.hexashoppingmallservice.dto.cart.CartDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CartServiceTest {

    @Mock
    private RedisTemplate<String, List<CartDTO>> redisTemplate;

    @Mock
    private ValueOperations<String, List<CartDTO>> valueOperations;

    @InjectMocks
    private CartService cartService;

    private final String memberId = "hexa";
    private final String key = "cart:" + memberId;
    private List<CartDTO> cartDtos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartDtos = List.of(
                new CartDTO(1L,2),
                new CartDTO(2L,1)
        );
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("장바구니 설정 테스트")
    void setCart() {
        cartService.setCart(String.valueOf(memberId), cartDtos);
        verify(valueOperations, times(1)).set(key, cartDtos, 1, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("장바구니 조회 테스트 - 데이터 존재")
    void getCartWithData() {
        when(valueOperations.get(key)).thenReturn(cartDtos);
        List<CartDTO> result = cartService.getCart(String.valueOf(memberId));
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

    }

    @Test
    @DisplayName("장바구니 조회 테스트 - 데이터 없음")
    void getCartNoData() {
        when(valueOperations.get(key)).thenReturn(null);
        List<CartDTO> result = cartService.getCart(String.valueOf(memberId));
        assertThat(result).isNull();
    }
}