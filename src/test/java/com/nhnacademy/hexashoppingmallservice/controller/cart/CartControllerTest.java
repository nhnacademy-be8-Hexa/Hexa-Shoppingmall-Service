package com.nhnacademy.hexashoppingmallservice.controller.cart;

import com.nhnacademy.hexashoppingmallservice.service.cart.CartService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtUtils jwtUtils;

    private final String memberId = "whskgys";

    @Test
    @DisplayName("GET /api/members/{memberId}/carts - 카트가 존재하는 경우")
    void testGetCartExists() throws Exception {
        // given
        String cartJson = "[{\"item\":\"book1\",\"quantity\":2},{\"item\":\"book2\",\"quantity\":1}]";
        when(cartService.getCart(memberId)).thenReturn(cartJson);

        // when
        ResultActions result = mockMvc.perform(get("/api/members/{memberId}/carts", memberId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(cartJson));

        verify(cartService, times(1)).getCart(memberId);
    }

    @Test
    @DisplayName("GET /api/members/{memberId}/carts - 카트가 존재하지 않는 경우")
    void testGetCartNotExists() throws Exception {
        // given
        String emptyCartJson = "[]";
        when(cartService.getCart(memberId)).thenReturn(emptyCartJson);

        // when
        ResultActions result = mockMvc.perform(get("/api/members/{memberId}/carts", memberId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(emptyCartJson));

        verify(cartService, times(1)).getCart(memberId);
    }

    @Test
    @DisplayName("PUT /api/members/{memberId}/carts - 카트 설정 성공")
    void testSetCartSuccess() throws Exception {
        // given
        String cartJson = "[{\"item\":\"book3\",\"quantity\":1}]";

        // when
        ResultActions result = mockMvc.perform(put("/api/members/{memberId}/carts", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cartJson));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("장바구니가 성공적으로 설정되었습니다."));

        ArgumentCaptor<String> memberIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cartCaptor = ArgumentCaptor.forClass(String.class);

        verify(cartService, times(1)).setCart(memberIdCaptor.capture(), cartCaptor.capture());

        assertEquals(memberId, memberIdCaptor.getValue());
        assertEquals(cartJson, cartCaptor.getValue());
    }



    @Test
    @DisplayName("PUT /api/members/{memberId}/carts - 빈 요청 본문")
    void testSetCartEmptyBody() throws Exception {
        // when
        ResultActions result = mockMvc.perform(put("/api/members/{memberId}/carts", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""));

        // then
        result.andExpect(status().isBadRequest());

        verify(cartService, times(0)).setCart(anyString(), anyString());
    }


}
