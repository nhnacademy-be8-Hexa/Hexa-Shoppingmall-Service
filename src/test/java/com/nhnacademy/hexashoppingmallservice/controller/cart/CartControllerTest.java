package com.nhnacademy.hexashoppingmallservice.controller.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.cart.CartRequestDTO;
import com.nhnacademy.hexashoppingmallservice.projection.cart.CartProjection;
import com.nhnacademy.hexashoppingmallservice.service.cart.CartService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private final String AUTH_HEADER = "Authorization";
    private final String BEARER_TOKEN = "Bearer dummy-token";

    @BeforeEach
    void setUp() {
        // 기본적으로 jwtUtils.ensureUserAccess가 호출될 때 아무 동작도 하지 않도록 설정
        doNothing().when(jwtUtils).ensureUserAccess(any(HttpServletRequest.class), any(String.class));
    }

    @Nested
    @DisplayName("GET /api/members/{memberId}/carts/{cartId}")
    class GetCartTests {

        @Test
        @DisplayName("성공적으로 특정 카트를 조회한다")
        void getCart_Success() throws Exception {
            // Arrange
            String memberId = "member123";
            Long cartId = 1L;

            // 실제 구현 객체를 생성하여 반환
            CartProjection cartProjection = new CartProjection() {
                @Override
                public Long getCartId() {
                    return cartId;
                }

                @Override
                public MemberProjection getMember() {
                    return new MemberProjection() {
                        @Override
                        public String getMemberId() {
                            return memberId;
                        }
                    };
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return 2L;
                        }

                        @Override
                        public String getBookTitle() {
                            return "Book Title";
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 3;
                }
            };

            when(cartService.getCart(cartId)).thenReturn(cartProjection);

            // Act & Assert
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}/carts/{cartId}", memberId, cartId)
                            .header(AUTH_HEADER, BEARER_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cartId").value(cartId))
                    .andExpect(jsonPath("$.member.memberId").value(memberId))
                    .andExpect(jsonPath("$.book.bookId").value(2L))
                    .andExpect(jsonPath("$.book.bookTitle").value("Book Title"))
                    .andExpect(jsonPath("$.cartAmount").value(3))
                    .andDo(document("get-cart",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("memberId").description("회원 ID"),
                                    parameterWithName("cartId").description("카트 ID")
                            ),
                            requestHeaders(
                                    headerWithName(AUTH_HEADER).description("인증 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("cartId").type(JsonFieldType.NUMBER).description("카트 ID"),
                                    fieldWithPath("member.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                                    fieldWithPath("book.bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                                    fieldWithPath("book.bookTitle").type(JsonFieldType.STRING).description("책 제목"),
                                    fieldWithPath("cartAmount").type(JsonFieldType.NUMBER).description("카트 수량")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("GET /api/members/{memberId}/carts")
    class GetCartsByMemberIdTests {

        @Test
        @DisplayName("성공적으로 특정 회원의 모든 카트를 조회한다")
        void getCartsByMemberId_Success() throws Exception {
            // Arrange
            String memberId = "member123";

            // 첫 번째 CartProjection
            CartProjection cartProjection1 = new CartProjection() {
                @Override
                public Long getCartId() {
                    return 1L;
                }

                @Override
                public MemberProjection getMember() {
                    return new MemberProjection() {
                        @Override
                        public String getMemberId() {
                            return memberId;
                        }
                    };
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return 1L;
                        }

                        @Override
                        public String getBookTitle() {
                            return "Book One";
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 2;
                }
            };

            // 두 번째 CartProjection
            CartProjection cartProjection2 = new CartProjection() {
                @Override
                public Long getCartId() {
                    return 2L;
                }

                @Override
                public MemberProjection getMember() {
                    return new MemberProjection() {
                        @Override
                        public String getMemberId() {
                            return memberId;
                        }
                    };
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return 2L;
                        }

                        @Override
                        public String getBookTitle() {
                            return "Book Two";
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 3;
                }
            };

            List<CartProjection> cartProjections = Arrays.asList(cartProjection1, cartProjection2);

            when(cartService.getCartByMemberId(memberId)).thenReturn(cartProjections);

            // Act & Assert
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}/carts", memberId)
                            .header(AUTH_HEADER, BEARER_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].cartId").value(1L))
                    .andExpect(jsonPath("$[0].member.memberId").value(memberId))
                    .andExpect(jsonPath("$[0].book.bookId").value(1L))
                    .andExpect(jsonPath("$[0].book.bookTitle").value("Book One"))
                    .andExpect(jsonPath("$[0].cartAmount").value(2))
                    .andExpect(jsonPath("$[1].cartId").value(2L))
                    .andExpect(jsonPath("$[1].member.memberId").value(memberId))
                    .andExpect(jsonPath("$[1].book.bookId").value(2L))
                    .andExpect(jsonPath("$[1].book.bookTitle").value("Book Two"))
                    .andExpect(jsonPath("$[1].cartAmount").value(3))
                    .andDo(document("get-carts-by-memberId",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("memberId").description("회원 ID")
                            ),
                            requestHeaders(
                                    headerWithName(AUTH_HEADER).description("인증 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("[].cartId").type(JsonFieldType.NUMBER).description("카트 ID"),
                                    fieldWithPath("[].member.memberId").type(JsonFieldType.STRING).description("회원 ID"),
                                    fieldWithPath("[].book.bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                                    fieldWithPath("[].book.bookTitle").type(JsonFieldType.STRING).description("책 제목"),
                                    fieldWithPath("[].cartAmount").type(JsonFieldType.NUMBER).description("카트 수량")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("POST /api/carts")
    class CreateCartTests {

        @Test
        @DisplayName("성공적으로 카트를 생성한다")
        void createCart_Success() throws Exception {
            // Arrange
            CartRequestDTO cartRequestDTO = new CartRequestDTO("member123", 1L, 2);

            doNothing().when(cartService).createCart(any(CartRequestDTO.class));

            // Act & Assert
            mockMvc.perform(RestDocumentationRequestBuilders.post("/api/carts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cartRequestDTO)))
                    .andExpect(status().isNoContent())
                    .andDo(document("create-cart",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("memberId").type(JsonFieldType.STRING).description("회원 ID"),
                                    fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                                    fieldWithPath("cartAmount").type(JsonFieldType.NUMBER).description("카트 수량")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("DELETE /api/members/{memberId}/carts/{cartId}")
    class DeleteCartTests {

        @Test
        @DisplayName("성공적으로 특정 카트를 삭제한다")
        void deleteCart_Success() throws Exception {
            // Arrange
            String memberId = "member123";
            Long cartId = 1L;

            doNothing().when(cartService).deleteCart(cartId);

            // Act & Assert
            mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/members/{memberId}/carts/{cartId}", memberId, cartId)
                            .header(AUTH_HEADER, BEARER_TOKEN))
                    .andExpect(status().isOk())
                    .andDo(document("delete-cart",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("memberId").description("회원 ID"),
                                    parameterWithName("cartId").description("카트 ID")
                            ),
                            requestHeaders(
                                    headerWithName(AUTH_HEADER).description("인증 토큰")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("DELETE /api/members/{memberId}/carts")
    class ClearCartByMemberTests {

        @Test
        @DisplayName("성공적으로 특정 회원의 모든 카트를 삭제한다")
        void clearCartByMember_Success() throws Exception {
            // Arrange
            String memberId = "member123";

            doNothing().when(cartService).clearCartByMember(memberId);

            // Act & Assert
            mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/members/{memberId}/carts", memberId)
                            .header(AUTH_HEADER, BEARER_TOKEN))
                    .andExpect(status().isNoContent())
                    .andDo(document("clear-cart-by-member",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("memberId").description("회원 ID")
                            ),
                            requestHeaders(
                                    headerWithName(AUTH_HEADER).description("인증 토큰")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("PUT /api/carts/{cartId}/quantity")
    class UpdateCartItemQuantityTests {

        @Test
        @DisplayName("성공적으로 카트 아이템의 수량을 업데이트한다")
        void updateCartItemQuantity_Success() throws Exception {
            // Arrange
            Long cartId = 1L;
            CartRequestDTO cartRequestDTO = new CartRequestDTO(null, null, 5);

            doNothing().when(cartService).updateCartItemQuantity(cartId, cartRequestDTO);

            // Act & Assert
            mockMvc.perform(RestDocumentationRequestBuilders.put("/api/carts/{cartId}/quantity", cartId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cartRequestDTO)))
                    .andExpect(status().isNoContent())
                    .andDo(document("update-cart-quantity",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("cartId").description("카트 ID")
                            ),
                            requestFields(
                                    fieldWithPath("cartAmount").type(JsonFieldType.NUMBER).description("카트 수량")
                            )
                    ));
        }

        @Test
        @DisplayName("유효하지 않은 요청으로 카트 수량 업데이트 시 검증 오류 발생")
        void updateCartItemQuantity_InvalidRequest() throws Exception {
            // Arrange
            Long cartId = 1L;
            CartRequestDTO cartRequestDTO = new CartRequestDTO();
            // cartAmount를 설정하지 않음

            // Act & Assert
            mockMvc.perform(RestDocumentationRequestBuilders.put("/api/carts/{cartId}/quantity", cartId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cartRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("update-cart-quantity-invalid",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("cartId").description("카트 ID")
                            ),
                            requestFields(
                                    fieldWithPath("cartAmount").type(JsonFieldType.NULL).description("카트 수량").optional()
                            )
                    ));
        }
    }
}