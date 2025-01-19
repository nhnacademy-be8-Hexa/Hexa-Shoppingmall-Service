package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.order.DeliveryCostPolicyRequest;
import com.nhnacademy.hexashoppingmallservice.entity.order.DeliveryCostPolicy;
import com.nhnacademy.hexashoppingmallservice.service.order.DeliveryCostPolicyService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = DeliveryCostPolicyController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class DeliveryCostPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryCostPolicyService deliveryCostPolicyService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        // 초기화 작업
    }

    @Test
    void createDeliveryCostPolicy() throws Exception {
        // 요청 데이터
        DeliveryCostPolicyRequest request = new DeliveryCostPolicyRequest(1000, 50000, "admin");

        // 반환될 응답 객체
        DeliveryCostPolicy deliveryCostPolicy =
                DeliveryCostPolicy.builder()
                        .deliveryCostPolicyId(1L)
                        .deliveryCost(1000)
                        .freeMinimumAmount(50000)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now()).build();

        doNothing().when(deliveryCostPolicyService).create(any(DeliveryCostPolicyRequest.class));

        mockMvc.perform(post("/api/delivery-cost-policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("create-delivery-cost-policy",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("deliveryCost").description("배달비"),
                                fieldWithPath("freeMinimumAmount").description("무료 배달 최소 금액"),
                                fieldWithPath("createdBy").description("생성자")
                        )

                ));
    }

    @Test
    void getRecentDeliveryCostPolicy() throws Exception {
        DeliveryCostPolicy policy =
                DeliveryCostPolicy.builder()
                        .deliveryCostPolicyId(1L)
                        .deliveryCost(1000)
                        .freeMinimumAmount(50000)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now()).build();
        given(deliveryCostPolicyService.getRecent()).willReturn(policy);

        mockMvc.perform(get("/api/delivery-cost-policy/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryCostPolicyId").value(1))
                .andExpect(jsonPath("$.deliveryCost").value(1000))
                .andExpect(jsonPath("$.freeMinimumAmount").value(50000))
                .andExpect(jsonPath("$.createdBy").value("admin"))
                .andDo(document("get-recent-delivery-cost-policy",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("deliveryCostPolicyId").description("배달비 정책 ID"),
                                fieldWithPath("deliveryCost").description("배달비"),
                                fieldWithPath("freeMinimumAmount").description("무료 배달 최소 금액"),
                                fieldWithPath("createdBy").description("생성자"),
                                fieldWithPath("createdAt").description("생성 일자")
                        )
                ));
    }

    @Test
    void getAllDeliveryCostPolicies() throws Exception {
        DeliveryCostPolicy policy1 =
                DeliveryCostPolicy.builder()
                        .deliveryCostPolicyId(1L)
                        .deliveryCost(1000)
                        .freeMinimumAmount(50000)
                        .createdBy("admin")
                        .createdAt(LocalDateTime.now()).build();

        DeliveryCostPolicy policy2 =
                DeliveryCostPolicy.builder()
                        .deliveryCostPolicyId(2L)
                        .deliveryCost(1500)
                        .freeMinimumAmount(70000)
                        .createdBy("admin2")
                        .createdAt(LocalDateTime.now()).build();


        given(deliveryCostPolicyService.getAllPaging(any())).willReturn(List.of(policy1, policy2));

        mockMvc.perform(get("/api/delivery-cost-policy/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].deliveryCostPolicyId").value(1))
                .andExpect(jsonPath("$[0].deliveryCost").value(1000))
                .andExpect(jsonPath("$[0].freeMinimumAmount").value(50000))
                .andExpect(jsonPath("$[0].createdBy").value("admin"))
                .andDo(document("get-all-delivery-cost-policies",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[].deliveryCostPolicyId").description("배달비 정책 ID"),
                                fieldWithPath("[].deliveryCost").description("배달비"),
                                fieldWithPath("[].freeMinimumAmount").description("무료 배달 최소 금액"),
                                fieldWithPath("[].createdBy").description("생성자"),
                                fieldWithPath("[].createdAt").description("생성 일자")
                        )
                ));
    }

    @Test
    void getCountDeliveryCostPolicies() throws Exception {
        Long count = 5L;

        given(deliveryCostPolicyService.countAll()).willReturn(count);

        mockMvc.perform(get("/api/delivery-cost-policy/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5))
                .andDo(document("get-count-delivery-cost-policies",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseBody()
                ));
    }
}
