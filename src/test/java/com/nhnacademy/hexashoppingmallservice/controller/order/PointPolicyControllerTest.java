package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.entity.order.PointPolicy;
import com.nhnacademy.hexashoppingmallservice.service.order.PointPolicyService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = PointPolicyController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class PointPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointPolicyService pointPolicyService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private PointPolicy pointPolicy;

    @BeforeEach
    void setUp() {
        pointPolicy = PointPolicy.builder()
                .pointPolicyName("defaultPolicy")
                .pointDelta(10)
                .build();
    }



    @Test
    void createPointPolicy() throws Exception {
        // Given: mock 서비스의 동작 정의
        PointPolicy pointPolicy = PointPolicy.builder()
                .pointPolicyName("defaultPolicy")
                .pointDelta(10)
                .build();

        given(pointPolicyService.createPointPolicy(any(PointPolicy.class))).willReturn(pointPolicy);

        // When: API 요청 수행
        mockMvc.perform(post("/api/pointPolicies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointPolicy)))  // request body에 pointPolicy 객체를 JSON으로 변환하여 전송
                .andExpect(status().isCreated())  // HTTP status 201 반환 기대
                .andExpect(jsonPath("$.pointPolicyName").value("defaultPolicy"))  // 응답에서 pointPolicyName 값이 "defaultPolicy"인지 확인
                .andExpect(jsonPath("$.pointDelta").value(10))  // 응답에서 pointDelta 값이 10인지 확인
                .andDo(document("create-point-policy",  // Spring REST Docs로 문서화
                        preprocessRequest(prettyPrint()),  // 요청 본문을 예쁘게 출력
                        preprocessResponse(prettyPrint()), // 응답 본문을 예쁘게 출력
                        requestFields(  // 요청 본문에 포함될 필드 정의
                                fieldWithPath("pointPolicyName").description("포인트 정책 이름"),
                                fieldWithPath("pointDelta").description("포인트 증감량")
                        ),
                        responseFields(  // 응답 본문에 포함될 필드 정의
                                fieldWithPath("pointPolicyName").description("포인트 정책 이름"),
                                fieldWithPath("pointDelta").description("포인트 증감량")
                        )
                ));
    }



    @Test
    void updatePointPolicy() throws Exception {
        // Given: mock 서비스의 동작 정의
        PointPolicy pointPolicy = PointPolicy.builder()
                .pointPolicyName("defaultPolicy")
                .pointDelta(10)
                .build();

        // Mocking service's update behavior
        given(pointPolicyService.updatePointPolicy(any(PointPolicy.class))).willReturn(pointPolicy);

        // When: API 요청 수행
        mockMvc.perform(put("/api/pointPolicies")  // PATCH 요청
                        .contentType(MediaType.APPLICATION_JSON)  // Content Type 설정
                        .content(objectMapper.writeValueAsString(pointPolicy)))  // 요청 본문에 pointPolicy 객체를 JSON으로 변환하여 전송
                .andExpect(status().isOk())  // HTTP status 200 OK 반환 기대
                .andExpect(jsonPath("$.pointPolicyName").value("defaultPolicy"))  // 응답에서 pointPolicyName 값이 "defaultPolicy"인지 확인
                .andExpect(jsonPath("$.pointDelta").value(10))  // 응답에서 pointDelta 값이 10인지 확인
                .andDo(document("update-point-policy",  // Spring REST Docs로 문서화
                        preprocessRequest(prettyPrint()),  // 요청 본문을 예쁘게 출력
                        preprocessResponse(prettyPrint()), // 응답 본문을 예쁘게 출력
                        requestFields(  // 요청 본문에 포함될 필드 정의
                                fieldWithPath("pointPolicyName").description("포인트 정책 이름"),
                                fieldWithPath("pointDelta").description("포인트 증감량")
                        ),
                        responseFields(  // 응답 본문에 포함될 필드 정의
                                fieldWithPath("pointPolicyName").description("포인트 정책 이름"),
                                fieldWithPath("pointDelta").description("포인트 증감량")
                        )
                ));
    }


    @Test
    void deletePointPolicy() throws Exception {
        mockMvc.perform(delete("/api/pointPolicies/{pointPolicyName}", "defaultPolicy"))
                .andExpect(status().isNoContent())
                .andDo(document("delete-point-policy",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("pointPolicyName").description("삭제할 포인트 정책 이름")
                        )
                ));
    }

    @Test
    void getAllPointPolicies() throws Exception {
        // Given: mock 서비스의 동작 정의
        List<PointPolicy> pointPolicies = List.of(
                PointPolicy.builder().pointPolicyName("policy1").pointDelta(5).build(),
                PointPolicy.builder().pointPolicyName("policy2").pointDelta(10).build()
        );

        given(pointPolicyService.getAllPointPolicies()).willReturn(pointPolicies);

        // When: API 요청 수행
        mockMvc.perform(get("/api/pointPolicies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pointPolicyName").value("policy1"))
                .andExpect(jsonPath("$[0].pointDelta").value(5))
                .andExpect(jsonPath("$[1].pointPolicyName").value("policy2"))
                .andExpect(jsonPath("$[1].pointDelta").value(10))
                .andDo(document("get-all-point-policies",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].pointPolicyName").description("포인트 정책 이름"),
                                fieldWithPath("[].pointDelta").description("포인트 증감량")
                        )
                ));
    }
}
