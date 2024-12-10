package com.nhnacademy.hexashoppingmallservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.entity.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.Role;
import com.nhnacademy.hexashoppingmallservice.service.MemberService;
import com.nhnacademy.hexashoppingmallservice.service.MemberStatusService;
import com.nhnacademy.hexashoppingmallservice.service.RatingService;
import jakarta.persistence.ManyToOne;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = MemberController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private RatingService ratingService;

    @MockBean
    private MemberStatusService memberStatusService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        Rating rating = new Rating("Gold", 10);
        MemberStatus status = new MemberStatus("Active");

        rating.setRatingId(1L);
        status.setStatusId(1L);

        given(ratingService.getRating(1L)).willReturn(rating);
        given(memberStatusService.getMemberStatus(1L)).willReturn(status);
    }

    @Test
    void getMembers() throws Exception {
        Rating rating = ratingService.getRating(1L);
        MemberStatus status = memberStatusService.getMemberStatus(1L);

        List<Member> members = List.of(
                createMockMember("test1", "John Doe", "01012345678", LocalDate.of(1990, 1, 1), rating, status),
                createMockMember("test2", "Jane Doe", "01098765432", LocalDate.of(1992, 2, 2), rating, status)
        );

        Page<Member> page = new PageImpl<>(members);
        given(memberService.findMembers(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/members")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(document("get-members", preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (기본값: 0)")
                        ),
                        responseFields(
                                fieldWithPath("[].memberId").description("회원 ID"),
                                fieldWithPath("[].memberPassword").description("회원 비밀번호"),
                                fieldWithPath("[].memberName").description("회원 이름"),
                                fieldWithPath("[].memberNumber").description("회원 전화번호"),
                                fieldWithPath("[].memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("[].memberCreatedAt").description("회원 생성일"),
                                fieldWithPath("[].memberLastLoginAt").description("회원 마지막 로그인 시간"),
                                fieldWithPath("[].memberRole").description("회원 권한"),
                                fieldWithPath("[].rating.ratingId").description("회원 등급 ID"),
                                fieldWithPath("[].rating.ratingName").description("회원 등급 이름"),
                                fieldWithPath("[].rating.ratingPercent").description("회원 등급 퍼센트"),
                                fieldWithPath("[].memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("[].memberStatus.statusName").description("회원 상태 이름")
                        )
                ));
    }

    @Test
    void getMember() throws Exception {
        Rating rating = ratingService.getRating(1L);
        MemberStatus status = memberStatusService.getMemberStatus(1L);

        Member member = createMockMember("test1", "John Doe", "01012345678", LocalDate.of(1990, 1, 1), rating, status);
        given(memberService.findMemberById("test1")).willReturn(Optional.of(member));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}", "test1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("test1"))
                .andExpect(jsonPath("$.memberName").value("John Doe"))
                .andExpect(jsonPath("$.memberRole").value("MEMBER"))
                .andExpect(jsonPath("$.rating.ratingName").value("Gold"))
                .andExpect(jsonPath("$.memberStatus.statusName").value("Active"))
                .andDo(document("get-member", preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("조회할 회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호"),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("memberCreatedAt").description("회원 생성일"),
                                fieldWithPath("memberLastLoginAt").description("회원 마지막 로그인 시간"),
                                fieldWithPath("memberRole").description("회원 권한"),
                                fieldWithPath("rating.ratingId").description("회원 등급 ID"),
                                fieldWithPath("rating.ratingName").description("회원 등급 이름"),
                                fieldWithPath("rating.ratingPercent").description("회원 등급 퍼센트"),
                                fieldWithPath("memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("memberStatus.statusName").description("회원 상태 이름")
                        )
                ));
    }

    @Test
    void createMember() throws Exception {
        Rating rating = ratingService.getRating(1L);
        MemberStatus status = memberStatusService.getMemberStatus(1L);
        MemberRequestDTO requestDTO = new MemberRequestDTO("test1", "password", "John Doe", "01012345678",
                LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1), LocalDateTime.now(), "ADMIN", "1", "1");
        Member member = new Member("test1", "password", "John Doe", "01012345678", LocalDate.of(1990, 1, 1),
                LocalDate.now(), LocalDateTime.now(), Role.MEMBER, rating, status);

        given(memberService.createMember(any(MemberRequestDTO.class))).willReturn(member);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value("test1"))
                .andExpect(jsonPath("$.memberName").value("John Doe"))
                .andDo(document("create-member", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), // 요청과 응답 모두 beautify
                        requestFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호"),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("memberCreatedAt").description("회원 가입일"),
                                fieldWithPath("memberLastLoginAt").description("회원 마지막 로그인 일시"),
                                fieldWithPath("memberRole").description("회원 권한"),
                                fieldWithPath("ratingId").description("회원 등급 ID"),
                                fieldWithPath("statusId").description("회원 상태 ID")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호"),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("memberCreatedAt").description("회원 가입일"),
                                fieldWithPath("memberLastLoginAt").description("회원 마지막 로그인 일시"),
                                fieldWithPath("memberRole").description("회원 권한"),
                                fieldWithPath("rating.ratingId").description("회원 등급 ID"),
                                fieldWithPath("rating.ratingName").description("회원 등급 이름"),
                                fieldWithPath("rating.ratingPercent").description("회원 등급 퍼센트"),
                                fieldWithPath("memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("memberStatus.statusName").description("회원 상태 이름")
                        )
                ));
    }

    /**
     * Mock Member 객체 생성 메서드
     */
    private Member createMockMember(String id, String name, String number, LocalDate birthDate, Rating rating, MemberStatus status) {
        return new Member(id, "password", name, number, birthDate,
                LocalDate.now(), LocalDateTime.now(), Role.MEMBER, rating, status);
    }
}