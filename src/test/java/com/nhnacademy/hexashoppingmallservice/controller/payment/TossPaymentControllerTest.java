package com.nhnacademy.hexashoppingmallservice.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.entity.payment.TossPayment;
import com.nhnacademy.hexashoppingmallservice.service.payment.TossPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TossPaymentController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TossPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TossPaymentService tossPaymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private TossPayment tossPayment;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        tossPayment = new TossPayment();
        Field orderIdIdField = tossPayment.getClass().getDeclaredField("orderId");
        orderIdIdField.setAccessible(true);
        orderIdIdField.set(tossPayment, 1L);

        Field paymentKeyField = tossPayment.getClass().getDeclaredField("paymentKey");
        paymentKeyField.setAccessible(true);
        paymentKeyField.set(tossPayment, "fjklsjfsfjsdklfjslkfjsdalkafjsklfj");

        Field amountField = tossPayment.getClass().getDeclaredField("amount");
        amountField.setAccessible(true);
        amountField.set(tossPayment, 10000);

    }

    /**
     * TossPayment 추가 테스트
     */
    @Test
    @DisplayName("POST /api/toss-payment - TossPayment 추가")
    void addPayment_Success() throws Exception {
        // Arrange
        Mockito.doNothing().when(tossPaymentService).create(any(TossPayment.class));

        // Act & Assert
        mockMvc.perform(post("/api/toss-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tossPayment)))
                .andExpect(status().isOk())
                .andDo(document("add-payment",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키")
                        )
                ));
    }

    /**
     * 특정 주문 ID에 대한 TossPayment 조회 테스트
     */
    @Test
    @DisplayName("GET /api/toss-payment/{orderId} - 특정 주문에 대한 TossPayment 조회")
    void getPayment_Success() throws Exception {
        // Arrange
        Long orderId = 123L;
        Mockito.when(tossPaymentService.getPayment(eq(orderId))).thenReturn(tossPayment);

        // Act & Assert
        mockMvc.perform(get("/api/toss-payment/{orderId}", orderId))
                .andExpect(status().isOk())
                .andDo(document("get-payment",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키")
                        )
                ));
    }
}
