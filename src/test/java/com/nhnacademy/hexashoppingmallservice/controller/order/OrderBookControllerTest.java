package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.book.OrderBookDTO;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderBookService;
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
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;


@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(OrderBookController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class OrderBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderBookService orderBookService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<OrderBookDTO> orderBookDTOs;

    @BeforeEach
    void setUp() {
        orderBookDTOs = Arrays.asList(
                new OrderBookDTO(1L, 101L, "Book Title 1", 2, 15000, 1L),
                new OrderBookDTO(1L, 102L, "Book Title 2", 1, 20000, 2L)
        );
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
    @DisplayName("getOrderBooks 메서드 테스트")
    class GetOrderBooksTests {

        @Test
        @DisplayName("성공적으로 주문 상세 정보를 조회한다")
        void getOrderBooks_Success() throws Exception {
            // Arrange
            Long orderId = 1L;
            when(orderBookService.getOrderBooksByOrderId(orderId)).thenReturn(orderBookDTOs);

            // Act & Assert
            mockMvc.perform(get("/api/orders/{orderId}/orderBooks", orderId)
                            .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].orderId").value(1L))
                    .andExpect(jsonPath("$[0].bookTitle").value("Book Title 1"))
                    .andExpect(jsonPath("$[0].orderBookAmount").value(2))
                    .andDo(document("get-order-books-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("orderId").description("주문 ID")
                            ),
                            responseFields(
                                    fieldWithPath("[].orderId").description("주문 ID"),
                                    fieldWithPath("[].bookId").description("책 ID"),
                                    fieldWithPath("[].bookTitle").description("책 제목"),
                                    fieldWithPath("[].orderBookAmount").description("주문 수량"),
                                    fieldWithPath("[].bookPrice").description("책 가격"),
                                    fieldWithPath("[].couponId").description("쿠폰 ID")
                            ),
                            requestHeaders(
                                    headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                            )
                    ));
        }


    }
}
