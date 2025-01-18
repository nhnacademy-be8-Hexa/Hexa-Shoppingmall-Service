package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.entity.member.*;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberCouponProjection;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberCouponService;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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

@WebMvcTest(controllers = MemberCouponController.class)
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberCouponControllerTest {

    // 프로젝션 인터페이스를 구현하는 테스트용 클래스
    @Getter
    @AllArgsConstructor
    static class TestMemberCouponProjection implements MemberCouponProjection {
        private Long memberCouponId;
        private Long couponId;
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberCouponService memberCouponService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private MemberService memberService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Member member;

    @BeforeEach
    void setUp() {
        // Mock Member 객체 생성
        Rating rating = Rating.builder()
                .ratingId(1L)
                .ratingName("Gold")
                .ratingPercent(10)
                .build();

        MemberStatus memberStatus = MemberStatus.builder()
                .statusId(1L)
                .statusName("Active")
                .build();

        member = Member.builder()
                .memberId("member123")
                .memberPassword("password123")
                .memberName("홍길동")
                .memberNumber("01012345678")
                .memberEmail("hong@example.com")
                .memberBirthAt(LocalDate.of(1990, 1, 1))
                .memberCreatedAt(LocalDateTime.now())
                .memberLastLoginAt(LocalDateTime.now())
                .memberRole(Role.MEMBER)
                .rating(rating)
                .memberStatus(memberStatus)
                .build();
    }

    /**
     * Helper method to create a MemberCouponProjection object
     */
    private MemberCouponProjection createMockMemberCouponProjection(Long id, Long couponId) {
        return new TestMemberCouponProjection(id, couponId);
    }

    @Test
    @DisplayName("getMemberCoupons: 성공 - 특정 회원의 쿠폰 목록을 반환함")
    void getMemberCoupons_Success() throws Exception {
        // Arrange
        List<MemberCouponProjection> mockCoupons = List.of(
                createMockMemberCouponProjection(1L, 100L),
                createMockMemberCouponProjection(2L, 101L)
        );
        given(memberCouponService.getMemberCoupon("member123")).willReturn(mockCoupons);

        // Act & Assert
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}/coupons", "member123")
                        .header("Authorization", "Bearer dummy-token") // Authorization 헤더 추가
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockCoupons.size()))
                .andExpect(jsonPath("$[0].memberCouponId").value(1))
                .andExpect(jsonPath("$[0].couponId").value(100))
                .andExpect(jsonPath("$[1].memberCouponId").value(2))
                .andExpect(jsonPath("$[1].couponId").value(101))
                .andDo(document("get-member-coupons",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("쿠폰을 조회할 회원의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        responseFields(
                                fieldWithPath("[].memberCouponId").description("회원 쿠폰 ID"),
                                fieldWithPath("[].couponId").description("쿠폰 ID")
                        )
                ));
        verify(memberCouponService).getMemberCoupon("member123");
    }

    @Test
    @DisplayName("createMemberCoupon: 성공 - 특정 회원에게 쿠폰을 생성함")
    void createMemberCoupon_Success() throws Exception {
        // Arrange
        String memberId = "member123";
        Long couponId = 200L;
        doNothing().when(memberCouponService).createMemberCoupon(couponId, memberId);

        // Act & Assert
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/members/{memberId}/coupons/{couponId}", memberId, couponId)
                        .header("Authorization", "Bearer admin-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("create-member-coupon",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("쿠폰을 생성할 회원의 ID"),
                                parameterWithName("couponId").description("생성할 쿠폰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        )
                ));
        verify(memberCouponService).createMemberCoupon(couponId, memberId);
    }

    @Test
    @DisplayName("deleteMemberCoupon: 성공 - 특정 회원의 쿠폰을 삭제함")
    void deleteMemberCoupon_Success() throws Exception {
        // Arrange
        String memberId = "member123";
        Long couponId = 200L;

        doNothing().when(memberCouponService).deleteMemberCoupon(couponId, memberId);

        // Act & Assert
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/members/{memberId}/coupons/{couponId}", memberId, couponId)
                        .header("Authorization", "Bearer admin-token") // Authorization 헤더 추가
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-member-coupon",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("쿠폰을 삭제할 회원의 ID"),
                                parameterWithName("couponId").description("삭제할 쿠폰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        )
                ));
        verify(memberCouponService).deleteMemberCoupon(couponId, memberId);
    }

    @Test
    @DisplayName("getAllCouponId: 성공 - 모든 쿠폰 ID를 반환함")
    void getAllCouponId_Success() throws Exception {
        // Arrange
        List<Long> mockCouponIds = List.of(100L, 101L, 102L);
        given(memberCouponService.getAllCouponId()).willReturn(mockCouponIds);

        // Act & Assert
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/All/coupons")
                        .header("Authorization", "Bearer dummy-token") // Authorization 헤더 추가
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockCouponIds.size()))
                .andExpect(jsonPath("$[0]").value(100))
                .andExpect(jsonPath("$[1]").value(101))
                .andExpect(jsonPath("$[2]").value(102))
                .andDo(document("get-all-coupon-ids",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        responseFields(
                                fieldWithPath("[].").description("쿠폰 ID 목록")
                        )
                ));
        verify(memberCouponService).getAllCouponId();
    }

}