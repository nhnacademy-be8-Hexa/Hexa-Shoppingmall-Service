package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.order.OrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.projection.order.OrderProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(OrderController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequestDTO validOrderRequestDTO;
    private List<Long> bookIds;
    private List<Integer> amounts;
    private Long couponId;

    @BeforeEach
    void setUp() {
        validOrderRequestDTO = new OrderRequestDTO(
                "member123",
                50000,
                1L,
                1L,
                "12345",
                "123 Main St",
                "Apt 101"
        );
        bookIds = Arrays.asList(1L, 2L);
        amounts = Arrays.asList(2, 3);
        couponId = 1L;
    }

    /**
     * Helper method to convert object to JSON string
     */
    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("createOrder 메서드 테스트")
    class CreateOrderTests {

        @Test
        @DisplayName("성공적으로 주문을 생성한다")
        void createOrder_Success() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(validOrderRequestDTO))
                            .param("bookIds", "1", "2")
                            .param("amounts", "2", "3")
                            .param("couponId", "1"))
                    .andExpect(status().isOk())
                    .andDo(document("create-order",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("memberId").description("회원 ID"),
                                    fieldWithPath("orderPrice").description("주문 가격"),
                                    fieldWithPath("wrappingPaperId").description("포장지 ID"),
                                    fieldWithPath("orderStatusId").description("주문 상태 ID"),
                                    fieldWithPath("zoneCode").description("우편번호").optional(),
                                    fieldWithPath("address").description("주소").optional(),
                                    fieldWithPath("addressDetail").description("상세 주소")
                            ),
                            queryParameters(
                                    parameterWithName("bookIds").description("주문한 책들의 ID 목록"),
                                    parameterWithName("amounts").description("각 책의 주문 수량"),
                                    parameterWithName("couponId").description("쿠폰 ID").optional()
                            )
                    ));
        }

        @Test
        @DisplayName("잘못된 요청 데이터로 주문 생성 시 검증 오류 발생")
        void createOrder_InvalidData() throws Exception {
            // Arrange
            OrderRequestDTO invalidOrderRequestDTO = new OrderRequestDTO(
                    "", // 빈 memberId
                    null, // null orderPrice
                    null, // null wrappingPaperId
                    null, // null orderStatusId
                    "123456", // zoneCode exceeds max length (assuming max 5)
                    "123 Main St",
                    "" // empty addressDetail
            );

            // Act & Assert
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(invalidOrderRequestDTO))
                            .param("bookIds", "1", "2")
                            .param("amounts", "2", "3"))
                    .andExpect(status().isBadRequest())
                    .andDo(document("create-order-invalid",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("memberId").description("회원 ID").optional(),
                                    fieldWithPath("orderPrice").description("주문 가격").optional(),
                                    fieldWithPath("wrappingPaperId").description("포장지 ID").optional(),
                                    fieldWithPath("orderStatusId").description("주문 상태 ID").optional(),
                                    fieldWithPath("zoneCode").description("우편번호").optional(),
                                    fieldWithPath("address").description("주소").optional(),
                                    fieldWithPath("addressDetail").description("상세 주소").optional()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("getAllOrders 메서드 테스트")
    class GetAllOrdersTests {

        @Test
        @DisplayName("성공적으로 모든 주문을 조회한다")
        void getAllOrders_Success() throws Exception {
            // Arrange
            List<OrderProjection> orders = Arrays.asList(
                    new OrderProjection() {
                        @Override
                        public Long getOrderId() { return 1L; }
                        @Override
                        public Integer getOrderPrice() { return 50000; }
                        @Override
                        public LocalDateTime getOrderedAt() { return LocalDateTime.now(); }
                        @Override
                        public WrappingPaper getWrappingPaper() { return null; }
                        @Override
                        public OrderStatus getOrderStatus() { return null; }
                        @Override
                        public String getZoneCode() { return "12345"; }
                        @Override
                        public String getAddress() { return "123 Main St"; }
                        @Override
                        public String getAddressDetail() { return "Apt 101"; }
                        @Override
                        public MemberProjection getMember() { return () -> "member123"; }
                    }
            );

            when(orderService.getAllOrders(any())).thenReturn(orders);

            // Act & Assert
            mockMvc.perform(get("/api/orders")
                            .param("page", "0")
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].orderId").value(1L))
                    .andExpect(jsonPath("$[0].orderPrice").value(50000))
                    .andDo(document("get-all-orders",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("page").description("페이지 번호")
                            ),
                            responseFields(
                                    fieldWithPath("[].orderId").description("주문 ID"),
                                    fieldWithPath("[].orderPrice").description("주문 가격"),
                                    fieldWithPath("[].orderedAt").description("주문 일시"),
                                    fieldWithPath("[].wrappingPaper").description("포장지 정보"),
                                    fieldWithPath("[].orderStatus").description("주문 상태 정보"),
                                    fieldWithPath("[].zoneCode").description("우편번호"),
                                    fieldWithPath("[].address").description("주소"),
                                    fieldWithPath("[].addressDetail").description("상세 주소"),
                                    fieldWithPath("[].member").description("회원 정보"),
                                    fieldWithPath("[].member.memberId").description("회원 ID")
                            ),
                            requestHeaders(
                                    headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("getOrdersByMemberId 메서드 테스트")
    class GetOrdersByMemberIdTests {

        @Test
        @DisplayName("성공적으로 특정 회원의 주문을 조회한다")
        void getOrdersByMemberId_Success() throws Exception {
            // Arrange
            String memberId = "member123";
            List<OrderProjection> orders = Arrays.asList(
                    new OrderProjection() {
                        @Override
                        public Long getOrderId() { return 1L; }
                        @Override
                        public Integer getOrderPrice() { return 50000; }
                        @Override
                        public LocalDateTime getOrderedAt() { return LocalDateTime.now(); }
                        @Override
                        public WrappingPaper getWrappingPaper() { return null; }
                        @Override
                        public OrderStatus getOrderStatus() { return null; }
                        @Override
                        public String getZoneCode() { return "12345"; }
                        @Override
                        public String getAddress() { return "123 Main St"; }
                        @Override
                        public String getAddressDetail() { return "Apt 101"; }
                        @Override
                        public MemberProjection getMember() { return () -> "member123"; }
                    }
            );

            when(orderService.getOrdersByMemberId(eq(memberId), any())).thenReturn(orders);

            // Act & Assert
            mockMvc.perform(get("/api/members/{memberId}/orders", memberId)
                            .param("page", "0")
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].orderId").value(1L))
                    .andDo(document("get-orders-by-memberId",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("memberId").description("회원 ID")
                            ),
                            queryParameters(
                                    parameterWithName("page").description("페이지 번호")
                            ),
                            responseFields(
                                    fieldWithPath("[].orderId").description("주문 ID"),
                                    fieldWithPath("[].orderPrice").description("주문 가격"),
                                    fieldWithPath("[].orderedAt").description("주문 일시"),
                                    fieldWithPath("[].wrappingPaper").description("포장지 정보"),
                                    fieldWithPath("[].orderStatus").description("주문 상태 정보"),
                                    fieldWithPath("[].zoneCode").description("우편번호"),
                                    fieldWithPath("[].address").description("주소"),
                                    fieldWithPath("[].addressDetail").description("상세 주소"),
                                    fieldWithPath("[].member").description("회원 정보"),
                                    fieldWithPath("[].member.memberId").description("회원 ID")
                            ),
                            requestHeaders(
                                    headerWithName("Authorization").description("멤버 인증 토큰 (Bearer 형식)")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("getOrderById 메서드 테스트")
    class GetOrderByIdTests {

        @Test
        @DisplayName("성공적으로 특정 주문을 조회한다")
        void getOrderById_Success() throws Exception {
            // Arrange
            Long orderId = 1L;
            OrderProjection order = new OrderProjection() {
                @Override
                public Long getOrderId() { return orderId; }
                @Override
                public Integer getOrderPrice() { return 50000; }
                @Override
                public LocalDateTime getOrderedAt() { return LocalDateTime.now(); }
                @Override
                public WrappingPaper getWrappingPaper() { return null; }
                @Override
                public OrderStatus getOrderStatus() { return null; }
                @Override
                public String getZoneCode() { return "12345"; }
                @Override
                public String getAddress() { return "123 Main St"; }
                @Override
                public String getAddressDetail() { return "Apt 101"; }
                @Override
                public MemberProjection getMember() { return () -> "member123"; }
            };

            when(orderService.getOrder(orderId)).thenReturn(order);

            // Act & Assert
            mockMvc.perform(get("/api/orders/{orderId}", orderId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderId").value(orderId))
                    .andDo(document("get-order-by-id",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("orderId").description("주문 ID")
                            ),
                            responseFields(
                                    fieldWithPath("orderId").description("주문 ID"),
                                    fieldWithPath("orderPrice").description("주문 가격"),
                                    fieldWithPath("orderedAt").description("주문 일시"),
                                    fieldWithPath("wrappingPaper").description("포장지 정보"),
                                    fieldWithPath("orderStatus").description("주문 상태 정보"),
                                    fieldWithPath("zoneCode").description("우편번호"),
                                    fieldWithPath("address").description("주소"),
                                    fieldWithPath("addressDetail").description("상세 주소"),
                                    fieldWithPath("member").description("회원 정보"),
                                    fieldWithPath("member.memberId").description("회원 ID")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("updateOrder 메서드 테스트")
    class UpdateOrderTests {

        @Test
        @DisplayName("성공적으로 주문을 업데이트한다")
        void updateOrder_Success() throws Exception {
            // Arrange
            Long orderId = 1L;
            OrderRequestDTO updateOrderRequestDTO = new OrderRequestDTO(
                    "member123",
                    60000,
                    2L,
                    2L,
                    "54321",
                    "456 Elm St",
                    "Suite 202"
            );

            // Act & Assert
            mockMvc.perform(put("/api/orders/{orderId}", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updateOrderRequestDTO)))
                    .andExpect(status().isNoContent())
                    .andDo(document("update-order",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("orderId").description("주문 ID")
                            ),
                            requestFields(
                                    fieldWithPath("memberId").description("회원 ID"),
                                    fieldWithPath("orderPrice").description("주문 가격"),
                                    fieldWithPath("wrappingPaperId").description("포장지 ID"),
                                    fieldWithPath("orderStatusId").description("주문 상태 ID"),
                                    fieldWithPath("zoneCode").description("우편번호").optional(),
                                    fieldWithPath("address").description("주소").optional(),
                                    fieldWithPath("addressDetail").description("상세 주소")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("getOrderAmount 메서드 테스트")
    class GetOrderAmountTests {

        @Test
        @DisplayName("성공적으로 특정 주문의 책 수량을 조회한다")
        void getOrderAmount_Success() throws Exception {
            // Arrange
            Long orderId = 1L;
            Long bookId = 2L;
            Long amount = 3L;

            when(orderService.getAmount(orderId, bookId)).thenReturn(amount);

            // Act & Assert
            mockMvc.perform(get("/api/orders/{orderId}/books/{bookId}", orderId, bookId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(amount.toString()))
                    .andDo(document("get-order-amount",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("orderId").description("주문 ID"),
                                    parameterWithName("bookId").description("책 ID")
                            ),
                            responseBody()
                    ));
        }
    }

    ///


    @Nested
    @DisplayName("getCheckOrderBook 메서드 테스트")
    class GetCheckOrderBookTests {

        @Test
        @DisplayName("특정 도서가 특정 회원의 주문에 존재하는지 확인한다")
        void getCheckOrderBook_Success() throws Exception {
            // Arrange
            String memberId = "member123";
            Long bookId = 2L;
            boolean orderExists = true; // 가정: 특정 도서가 주문에 존재한다고 응답

            when(orderService.checkOrderBook(eq(memberId), eq(bookId))).thenReturn(orderExists);

            // Act & Assert
            mockMvc.perform(get("/api/members/{memberId}/orders/books/{bookId}", memberId, bookId)
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(Boolean.toString(orderExists)))
                    .andDo(document("get-check-order-book",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("memberId").description("회원 ID"),
                                    parameterWithName("bookId").description("도서 ID")
                            ),
                            responseBody()
                    ));
        }
    }


    @Nested
    @DisplayName("existsOrderIdAndMember_MemberId 메서드 테스트")
    class ExistsOrderIdAndMember_MemberIdTests {

        @Test
        @DisplayName("주문 ID와 회원 ID가 일치하는지 확인한다")
        void existsOrderIdAndMember_MemberId_Success() throws Exception {
            // Arrange
            Long orderId = 1L;
            String memberId = "member123";
            boolean exists = true; // 가정: orderId와 memberId가 일치한다고 응답

            when(orderService.existsOrderIdAndMember_MemberId(eq(orderId), eq(memberId))).thenReturn(exists);

            // Act & Assert
            mockMvc.perform(get("/api/orders/{orderId}/{memberId}", orderId, memberId)
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(Boolean.toString(exists)))
                    .andDo(document("exists-order-id-and-member-id",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("orderId").description("주문 ID"),
                                    parameterWithName("memberId").description("회원 ID")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("countAllByMember_MemberId 메서드 테스트")
    class CountAllByMember_MemberIdTests {

        @Test
        @DisplayName("특정 회원의 전체 주문 수량을 조회한다")
        void countAllByMember_MemberId_Success() throws Exception {
            // Arrange
            String memberId = "member123";
            Long orderCount = 5L; // 가정: 특정 회원의 주문 수는 5

            when(orderService.countAllByMember_MemberId(eq(memberId))).thenReturn(orderCount);

            // Act & Assert
            mockMvc.perform(get("/api/orders/count/{memberId}", memberId)
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(orderCount.toString()))
                    .andDo(document("count-all-by-member-id",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("memberId").description("회원 ID")
                            ),
                            responseBody()
                    ));
        }
    }


    @Nested
    @DisplayName("countOrdersByStatus 메서드 테스트")
    class CountOrdersByStatusTests {

        @Test
        @DisplayName("주문 상태별 주문 수량을 조회한다")
        void countOrdersByStatus_Success() throws Exception {
            // Arrange
            Long statusId = 1L;
            Long orderCount = 10L; // 가정: 특정 상태의 주문 수는 10

            when(orderService.countOrdersByStatusId(eq(statusId))).thenReturn(orderCount);

            // Act & Assert
            mockMvc.perform(get("/api/orders/status/{statusId}/count", statusId)
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(orderCount.toString()))
                    .andDo(document("count-orders-by-status",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("statusId").description("주문 상태 ID")
                            ),
                            responseBody()
                    ));
        }
    }



}