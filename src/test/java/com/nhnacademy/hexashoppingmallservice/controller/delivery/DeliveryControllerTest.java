package com.nhnacademy.hexashoppingmallservice.controller.delivery;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.controller.order.DeliveryController;
import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.order.Delivery;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.projection.order.DeliveryProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.DeliveryService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = DeliveryController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class DeliveryControllerTest {

    @Getter
    @AllArgsConstructor
    static class TestDeliveryProjection implements DeliveryProjection {
        private Integer deliveryAmount;
        private LocalDateTime deliveryDate;
        private LocalDateTime deliveryReleaseDate;

        @Override
        public OrderProjection getOrder() {
            return new TestOrderProjection(1L);
        }
    }

    @Getter
    @AllArgsConstructor
    static class TestOrderProjection implements DeliveryProjection.OrderProjection {

        private Long orderId;

        @Override
        public Long getOrderId() {
            return orderId;
        }
    }


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private DeliveryRequestDTO deliveryRequestDTO;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Member member = mock(Member.class);
        WrappingPaper wrappingPaper = mock(WrappingPaper.class);
        OrderStatus orderStatus = mock(OrderStatus.class);

        Order order = Order.of(member, 10000, wrappingPaper, orderStatus, "12345", "address", "addressDetail");

        Field orderIdField = order.getClass().getDeclaredField("orderId");
        orderIdField.setAccessible(true);
        orderIdField.set(order, 1L);

        deliveryRequestDTO = new DeliveryRequestDTO(1L, 1000, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        Delivery delivery = Delivery.of(order, deliveryRequestDTO.getDeliveryAmount(), deliveryRequestDTO.getDeliveryDate(), deliveryRequestDTO.getDeliveryReleaseDate());
        Field deliveryIdField = delivery.getClass().getDeclaredField("orderId");
        deliveryIdField.setAccessible(true);
        deliveryIdField.set(delivery, order.getOrderId());
    }

    @Test
    void createDelivery() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/deliveries")
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequestDTO)))
                .andExpect(status().isNoContent())
                .andDo(document("create-delivery",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token for authorization")
                        ),
                        requestFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("deliveryAmount").description("배송비"),
                                fieldWithPath("deliveryDate").description("배송일"),
                                fieldWithPath("deliveryReleaseDate").description("출고일")
                        )
                ));
    }

    @Test
    void getAllDeliveries() throws Exception {
        TestDeliveryProjection testDeliveryProjection =
                new TestDeliveryProjection(1000, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        given(deliveryService.getDeliveries(any(Pageable.class))).willReturn(List.of(testDeliveryProjection));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/deliveries")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("[0].order.orderId").value(1L))
                .andExpect(jsonPath("[0].deliveryAmount").value(1000))
                .andExpect(jsonPath("[0].deliveryDate").exists())
                .andExpect(jsonPath("[0].deliveryReleaseDate").exists())
                .andDo(document("get-all-deliveries",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token for authorization")
                        ),
                        responseFields(
                                fieldWithPath("[].order.orderId").description("주문 ID"),
                                fieldWithPath("[].deliveryAmount").description("배송비"),
                                fieldWithPath("[].deliveryDate").description("배송일"),
                                fieldWithPath("[].deliveryReleaseDate").description("출고일")
                        )
                ));
    }

    @Test
    void getDeliveryByOrderId() throws Exception {

        TestDeliveryProjection testDeliveryProjection =
                new TestDeliveryProjection(1000, LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        given(deliveryService.getDeliveryByOrderId(1L)).willReturn(testDeliveryProjection);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/{orderId}/deliveries", 1L)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.orderId").value(1L))
                .andExpect(jsonPath("$.deliveryAmount").value(1000))
                .andExpect(jsonPath("$.deliveryDate").exists())
                .andExpect(jsonPath("$.deliveryReleaseDate").exists())
                .andDo(document("get-delivery-by-order-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token for authorization")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("order.orderId").description("주문 ID"),
                                fieldWithPath("deliveryAmount").description("배송비"),
                                fieldWithPath("deliveryDate").description("배송일"),
                                fieldWithPath("deliveryReleaseDate").description("출고일")
                        )
                ));
    }

    @Test
    void getDeliveriesByMemberId() throws Exception {

        TestDeliveryProjection testDeliveryProjection =
                new TestDeliveryProjection(1000, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        given(deliveryService.getDeliveriesByMemberId(eq("member1"), any(Pageable.class))).willReturn(
                List.of(testDeliveryProjection));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}/deliveries", "member1")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("[0].order.orderId").value(1L))
                .andExpect(jsonPath("[0].deliveryAmount").value(1000))
                .andExpect(jsonPath("[0].deliveryDate").exists())
                .andExpect(jsonPath("[0].deliveryReleaseDate").exists())
                .andDo(document("get-deliveries-by-member-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token for authorization")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].order.orderId").description("주문 ID"),
                                fieldWithPath("[].deliveryAmount").description("배송비"),
                                fieldWithPath("[].deliveryDate").description("배송일"),
                                fieldWithPath("[].deliveryReleaseDate").description("출고일")
                        )
                ));
    }

    @Test
    void updateDelivery() throws Exception {
        DeliveryRequestDTO updateDeliveryRequestDTO =
                new DeliveryRequestDTO(1L, 1000, LocalDateTime.now(), LocalDateTime.now().plusDays(5));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/orders/{orderId}/deliveries", 1L)
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDeliveryRequestDTO)))
                .andExpect(status().isNoContent())
                .andDo(document("update-delivery",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token for authorization")
                        ),
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        requestFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("deliveryAmount").description("배송비"),
                                fieldWithPath("deliveryDate").description("배송일"),
                                fieldWithPath("deliveryReleaseDate").description("출고일")
                        )
                ));
    }
}
