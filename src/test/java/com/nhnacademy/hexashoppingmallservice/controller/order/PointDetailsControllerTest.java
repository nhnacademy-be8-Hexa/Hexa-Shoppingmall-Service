package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.nhnacademy.hexashoppingmallservice.dto.order.CreatePointDetailDTO;
import com.nhnacademy.hexashoppingmallservice.dto.order.PointDetailsRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.member.Role;
import com.nhnacademy.hexashoppingmallservice.entity.order.PointDetails;
import com.nhnacademy.hexashoppingmallservice.service.order.PointDetailsService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.projection.order.PointDetailsProjection;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import java.util.Arrays;



import static org.mockito.BDDMockito.given;


@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = PointDetailsController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class PointDetailsControllerTest {

    static class PointDetailsProjectionImpl implements PointDetailsProjection {


        private final Long pointDetailsId;
        private final Integer pointDetailsIncrement;
        private final String pointDetailsComment;
        private final LocalDateTime pointDetailsDatetime;

        public PointDetailsProjectionImpl(Long pointDetailsId, Integer pointDetailsIncrement, String pointDetailsComment, LocalDateTime pointDetailsDatetime) {
            this.pointDetailsId = pointDetailsId;
            this.pointDetailsIncrement = pointDetailsIncrement;
            this.pointDetailsComment = pointDetailsComment;
            this.pointDetailsDatetime = pointDetailsDatetime;
        }

        public Long getPointDetailsId() {
            return pointDetailsId;
        }

        public Integer getPointDetailsIncrement() {
            return pointDetailsIncrement;
        }

        public String getPointDetailsComment() {
            return pointDetailsComment;
        }

        public LocalDateTime getPointDetailsDatetime() {
            return pointDetailsDatetime;
        }
    }

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointDetailsService pointDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        // 초기화 작업
    }

    @Test
    void createPointDetails() throws Exception {

        Rating rating = Rating.builder()
                .ratingId(1L)
                .ratingName("test-rating1")
                .ratingPercent(10)
                .build();

        MemberStatus memberStatus = MemberStatus.builder()
                .statusId(1L)
                .statusName("test-status1")
                .build();

        // 응답에 사용될 PointDetails 객체 (응답에서 member는 값이 있음)
        Member member = Member.builder()
                .memberId("1")
                .memberName("test1")
                .memberNumber("01012345678")
                .memberEmail("aaaa@naver.com")
                .memberBirthAt(LocalDate.now())
                .memberCreatedAt(LocalDateTime.now())
                .memberRole(Role.MEMBER)
                .rating(rating)
                .memberStatus(memberStatus)
                .memberPassword("1111")
                .memberLastLoginAt(LocalDateTime.now())
                .build();

        PointDetails pointDetails = PointDetails.builder()
                .pointDetailsId(1L)  // 응답에서는 아이디가 포함됨
                .pointDetailsIncrement(1000)
                .pointDetailsComment("1000 증가")
                .pointDetailsDatetime(LocalDateTime.now())
                .member(member)  // 응답 본문에 member 포함
                .build();

        CreatePointDetailDTO createPointDetailDTO = new CreatePointDetailDTO(1000 , "1000증가");

        given(pointDetailsService.createPointDetails(any(CreatePointDetailDTO.class), any(String.class)))
                .willReturn(pointDetails);

        mockMvc.perform(post("/api/members/{memberId}/pointDetails", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPointDetailDTO)))
                .andExpect(status().isCreated())
                .andDo(document("create-point-details",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("멤버 ID")
                        ),
                        requestFields(
                                fieldWithPath("pointDetailsIncrement").description("포인트 증감량"),
                                fieldWithPath("pointDetailsComment").description("포인트 상세 내역")
                        ),
                        responseFields(
                                fieldWithPath("pointDetailsId").description("포인트 내역 아이디"),
                                fieldWithPath("pointDetailsIncrement").description("포인트 증감량"),
                                fieldWithPath("pointDetailsComment").description("포인트 상세 내역"),
                                fieldWithPath("pointDetailsDatetime").description("포인트 상세 내역 시간"),

                                // 응답 본문에서 사용될 member 필드 (모든 값이 채워짐)
                                fieldWithPath("member.memberId").description("회원 아이디"),
                                fieldWithPath("member.memberPassword").description("회원 비밀번호"),
                                fieldWithPath("member.memberName").description("회원 이름"),
                                fieldWithPath("member.memberNumber").description("회원 전화번호"),
                                fieldWithPath("member.memberEmail").description("회원 이메일"),
                                fieldWithPath("member.memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("member.memberCreatedAt").description("회원 가입일"),
                                fieldWithPath("member.memberRole").description("회원 역할"),
                                fieldWithPath("member.rating").description("회원 등급").optional(),
                                fieldWithPath("member.rating.ratingId").description("회원 등급 아이디").optional(),
                                fieldWithPath("member.rating.ratingName").description("회원 등급 이름").optional(),
                                fieldWithPath("member.rating.ratingPercent").description("회원 등급 비율").optional(),
                                fieldWithPath("member.memberStatus").description("회원 상태").optional(),
                                fieldWithPath("member.memberStatus.statusId").description("회원 상태 아이디").optional(),
                                fieldWithPath("member.memberStatus.statusName").description("회원 상태 이름").optional(),
                                fieldWithPath("member.memberLastLoginAt").description("회원 마지막 로그인 시간")
                        )
                ));
    }



    @Test
    void getPointDetails() throws Exception {
        List<PointDetailsProjection> pointDetailsList = Arrays.asList(
                new PointDetailsProjectionImpl(1L, 1000, "포인트 적립", LocalDateTime.now()),
                new PointDetailsProjectionImpl(2L, -500, "포인트 사용", LocalDateTime.now())
        );

        // 포인트 상세 목록 조회
        given(pointDetailsService.getPointDetails(any(), any(String.class)))
                .willReturn(pointDetailsList);

        mockMvc.perform(get("/api/members/{memberId}/pointDetails", "testMemberId")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(pointDetailsList.size()))
                .andExpect(jsonPath("$[0].pointDetailsId").value(1))
                .andExpect(jsonPath("$[0].pointDetailsIncrement").value(1000))
                .andExpect(jsonPath("$[0].pointDetailsComment").value("포인트 적립"))
                .andDo(document("get-point-details",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("멤버 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].pointDetailsId").description("포인트 상세 ID"),
                                fieldWithPath("[].pointDetailsIncrement").description("포인트 증감량"),
                                fieldWithPath("[].pointDetailsComment").description("포인트 설명"),
                                fieldWithPath("[].pointDetailsDatetime").description("포인트 발생 시간")
                        )
                ));
    }



    @Test
    void sumPoint_returnsCorrectSum() throws Exception {
        Long expectedSum = 1500L;

        // 서비스 호출 Mock 설정
        given(pointDetailsService.sumPoint("1")).willReturn(expectedSum);

        mockMvc.perform(get("/api/members/{memberId}/pointDetails/sum", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 200 OK 응답 검증
                .andExpect(jsonPath("$").value(expectedSum))  // 반환된 합계 값 검증
                .andDo(document("sum-point",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("멤버 ID") // 경로 매개변수 문서화
                        )
                ));
    }

}

