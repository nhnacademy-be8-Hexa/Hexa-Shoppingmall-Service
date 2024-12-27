package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.nhnacademy.hexashoppingmallservice.service.member.MemberReportService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberReportController.class)
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class MemberReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberReportService memberReportService;

    @MockBean
    private JwtUtils jwtUtils;

    private String memberId;
    private Long reviewId;

    @BeforeEach
    void setUp() {
        memberId = "member123";
        reviewId = 1L;
    }

    /**
     * 새로운 회원 신고를 성공적으로 생성하는 테스트
     */
    @Test
    @DisplayName("POST /api/reports/members/{memberId}/reviews/{reviewId} - 성공적으로 신고를 생성한다")
    void saveMemberReport_Success() throws Exception {

        // MemberReportService가 신고를 정상적으로 처리하도록 모킹
        doNothing().when(memberReportService).saveMemberReport(memberId, reviewId);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/reports/members/{memberId}/reviews/{reviewId}", memberId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.jwt.token"))
                .andExpect(status().isCreated())
                .andDo(document("save-member-report",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("신고를 작성할 회원의 ID"),
                                parameterWithName("reviewId").description("신고할 리뷰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")
                        )
                ));
    }

    /**
     * 특정 리뷰의 신고 총계를 성공적으로 조회하는 테스트
     */
    @Test
    @DisplayName("GET /api/admin/reports/reviews/{reviewId}/count - 특정 리뷰의 신고 총계를 성공적으로 조회한다")
    void getTotalReport_Success() throws Exception {
        Long totalReport = 5L;


        // MemberReportService가 신고 총계를 정상적으로 반환하도록 모킹
        Mockito.when(memberReportService.totalReport(reviewId)).thenReturn(totalReport);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reports/reviews/{reviewId}/count", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer admin.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(content().string(totalReport.toString()))
                .andDo(document("get-total-report",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("신고 총계를 조회할 리뷰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")
                        ),
                        responseBody() // 응답 본문을 문서화
                ));
    }

    /**
     * 관리자용 특정 리뷰의 모든 신고를 성공적으로 삭제하는 테스트
     */
    @Test
    @DisplayName("DELETE /api/admin/reports/admin/reviews/{reviewId} - 특정 리뷰의 모든 신고를 성공적으로 삭제한다")
    void deleteAllReports_Success() throws Exception {

        // MemberReportService가 모든 신고를 정상적으로 삭제하도록 모킹
        doNothing().when(memberReportService).allDelete(reviewId);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/reports/admin/reviews/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer admin.jwt.token"))
                .andExpect(status().isNoContent())
                .andDo(document("delete-all-reports",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("삭제할 리뷰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")
                        )
                ));
    }
}