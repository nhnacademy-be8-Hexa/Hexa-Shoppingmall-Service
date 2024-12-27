package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.order.OrderStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.service.order.OrderStatusService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = OrderStatusController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class OrderStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderStatusService orderStatusService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("Get All Order Statuses - Success")
    void getAllOrderStatusSuccess() throws Exception {

        OrderStatus orderStatus1 = OrderStatus.of("Status1");
        OrderStatus orderStatus2 = OrderStatus.of("Status2");

        Field orderStatusIdField = orderStatus1.getClass().getDeclaredField("orderStatusId");
        orderStatusIdField.setAccessible(true);
        orderStatusIdField.set(orderStatus1, 1L);

        Field orderStatusIdField2 = orderStatus2.getClass().getDeclaredField("orderStatusId");
        orderStatusIdField2.setAccessible(true);
        orderStatusIdField2.set(orderStatus2, 2L);


        List<OrderStatus> orderStatuses = List.of(
                orderStatus1, orderStatus2
        );

        when(orderStatusService.getAllOrderStatus()).thenReturn(orderStatuses);

        mockMvc.perform(get("/api/orderStatus")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"orderStatusId\": 1, \"orderStatus\": \"Status1\"}, {\"orderStatusId\": 2, \"orderStatus\": \"Status2\"}]"))
                .andDo(document("get-all-order-statuses",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("[].orderStatusId").description("주문 상태 ID"),
                                PayloadDocumentation.fieldWithPath("[].orderStatus").description("주문 상태 이름")
                        )
                ));
    }

//    @Test
//    @DisplayName("Get All Order Statuses - Empty List")
//    void getAllOrderStatusEmptyList() throws Exception {
//        when(orderStatusService.getAllOrderStatus()).thenReturn(List.of());
//
//        mockMvc.perform(get("/api/orderStatus")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json("[]"))
//                .andDo(document("get-all-order-statuses-empty",
//                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())
//                ));
//    }

    @Test
    @DisplayName("Create Order Status - Success")
    void createOrderStatusSuccess() throws Exception {
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO("New Status");
        OrderStatus createdOrderStatus = new OrderStatus();
        createdOrderStatus.setOrderStatus("New Status");
        Field orderStatusIdField = createdOrderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusIdField.setAccessible(true);
        orderStatusIdField.set(createdOrderStatus, 1L);


        when(orderStatusService.createOrderStatus(Mockito.any(OrderStatusRequestDTO.class)))
                .thenReturn(createdOrderStatus);

        mockMvc.perform(post("/api/orderStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderStatus\": \"New Status\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"orderStatusId\": 1, \"orderStatus\": \"New Status\"}"))
                .andDo(document("create-order-status",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("orderStatus").description("주문 상태 이름")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("orderStatusId").description("주문 상태 ID"),
                                PayloadDocumentation.fieldWithPath("orderStatus").description("주문 상태 이름")
                        )
                ));
    }

    @Test
    @DisplayName("Get Order Status - Success")
    void getOrderStatusSuccess() throws Exception {
        Long orderStatusId = 1L;
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus("Fetched Status");

        Field orderStatusIdField = orderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusIdField.setAccessible(true);
        orderStatusIdField.set(orderStatus, 1L);

        when(orderStatusService.getOrderStatus(orderStatusId)).thenReturn(orderStatus);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orderStatus/{orderStatusId}", orderStatusId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"orderStatusId\":1,\"orderStatus\":\"Fetched Status\"}"))
                .andDo(document("get-order-status",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                RequestDocumentation.parameterWithName("orderStatusId").description("주문 상태 ID")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("orderStatusId").description("주문 상태 ID"),
                                PayloadDocumentation.fieldWithPath("orderStatus").description("주문 상태 이름")
                        )
                ));
    }

    @Test
    @DisplayName("Update Order Status - Success")
    void updateOrderStatusSuccess() throws Exception {
        Long orderStatusId = 1L;
        OrderStatusRequestDTO requestDTO = new OrderStatusRequestDTO("Updated Status");
        OrderStatus updatedOrderStatus = new OrderStatus();
        updatedOrderStatus.setOrderStatus("Updated Status");

        Field orderStatusIdField = updatedOrderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusIdField.setAccessible(true);
        orderStatusIdField.set(updatedOrderStatus, 1L);

        when(orderStatusService.updateOrderStatus(Mockito.eq(orderStatusId), Mockito.any(OrderStatusRequestDTO.class)))
                .thenReturn(updatedOrderStatus);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/orderStatus/{orderStatusId}", orderStatusId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderStatus\": \"Updated Status\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"orderStatusId\": 1, \"orderStatus\": \"Updated Status\"}"))
                .andDo(document("update-order-status",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                RequestDocumentation.parameterWithName("orderStatusId").description("주문 상태 ID")
                        ),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("orderStatus").description("주문 상태 이름")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("orderStatusId").description("주문 상태 ID"),
                                PayloadDocumentation.fieldWithPath("orderStatus").description("주문 상태 이름")
                        )
                ));
    }

    @Test
    @DisplayName("Delete Order Status - Success")
    void deleteOrderStatusSuccess() throws Exception {
        Long orderStatusId = 1L;
        OrderStatus fetchedOrderStatus = new OrderStatus();
        fetchedOrderStatus.setOrderStatus("Existing Status");

        Field orderStatusIdField = fetchedOrderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusIdField.setAccessible(true);
        orderStatusIdField.set(fetchedOrderStatus, 1L);

        when(orderStatusService.getOrderStatus(orderStatusId)).thenReturn(fetchedOrderStatus);
        Mockito.doNothing().when(orderStatusService).deleteOrderStatus(orderStatusId);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/orderStatus/{orderStatusId}", orderStatusId))
                .andExpect(status().isNoContent())
                .andDo(document("delete-order-status",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                RequestDocumentation.parameterWithName("orderStatusId").description("주문 상태 ID")
                        )
                ));
    }
}
