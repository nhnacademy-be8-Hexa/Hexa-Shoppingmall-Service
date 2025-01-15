package com.nhnacademy.hexashoppingmallservice.controller.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.cart.CartDTO;
import com.nhnacademy.hexashoppingmallservice.exception.InvalidTokenException;
import com.nhnacademy.hexashoppingmallservice.service.cart.CartService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/members/{memberId}/carts - 성공")
    void getCart_Success() throws Exception {
        String memberId = "hexa";
        List<CartDTO> cartDtos = List.of(
                new CartDTO(101L, 2),
                new CartDTO(102L, 1)
        );

        // Mock JwtUtils to do nothing (i.e., pass)
        doNothing().when(jwtUtils).ensureUserAccess(any(HttpServletRequest.class), eq(memberId));

        // Mock CartService to return cartDtos
        when(cartService.getCart(memberId)).thenReturn(cartDtos);

        mockMvc.perform(get("/api/members/{memberId}/carts", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cartDtos)));

        // Verify interactions
        verify(jwtUtils, times(1)).ensureUserAccess(any(HttpServletRequest.class), eq(memberId));
        verify(cartService, times(1)).getCart(memberId);
    }

    @Test
    @DisplayName("GET /api/members/{memberId}/carts - JwtUtils 예외 발생 시")
    void getCart_JwtException() throws Exception {
        String memberId = "hexa";

        // Mock JwtUtils to throw an exception
        doThrow(new InvalidTokenException("Unauthorized")).when(jwtUtils).ensureUserAccess(any(HttpServletRequest.class), eq(memberId));

        mockMvc.perform(get("/api/members/{memberId}/carts", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unauthorized"));

        // Verify interactions
        verify(jwtUtils, times(1)).ensureUserAccess(any(HttpServletRequest.class), eq(memberId));
        verify(cartService, never()).getCart(anyString());
    }

    @Test
    @DisplayName("PUT /api/members/{memberId}/carts - 성공")
    void setCart_Success() throws Exception {
        String memberId = "hexa";
        List<CartDTO> cartDtos = List.of(
                new CartDTO(101L, 2),
                new CartDTO(102L, 1)
        );

        // Mock CartService to do nothing
        doNothing().when(cartService).setCart(memberId, cartDtos);

        mockMvc.perform(put("/api/members/{memberId}/carts", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDtos)))
                .andExpect(status().isOk())
                .andExpect(content().string("장바구니가 성공적으로 설정되었습니다."));

        // Verify interactions
        verify(cartService, times(1)).setCart(memberId, cartDtos);
    }

    @Test
    @DisplayName("PUT /api/members/{memberId}/carts - 유효성 검증 실패")
    void setCart_ValidationFailure() throws Exception {
        String memberId = "hexa";
        List<CartDTO> cartDtos = List.of(
                new CartDTO(null, 2), // productId null
                new CartDTO(102L, 0)  // quantity < 1
        );

        mockMvc.perform(put("/api/members/{memberId}/carts", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDtos)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        // Verify that service was never called due to validation failure
        verify(cartService, never()).setCart(anyString(), anyList());
    }

    @Test
    @DisplayName("PUT /api/members/{memberId}/carts - 서비스 예외 발생 시")
    void setCart_ServiceException() throws Exception {
        String memberId = "hexa";
        List<CartDTO> cartDtos = List.of(
                new CartDTO(101L, 2),
                new CartDTO(102L, 1)
        );

        // Mock CartService to throw an exception
        doThrow(new RuntimeException("Redis Error")).when(cartService).setCart(memberId, cartDtos);

        mockMvc.perform(put("/api/members/{memberId}/carts", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDtos)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Redis Error"));

        // Verify interactions
        verify(cartService, times(1)).setCart(memberId, cartDtos);
    }
}