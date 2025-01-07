package com.nhnacademy.hexashoppingmallservice.controller.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.book.MemberUpdateDTO;
import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.member.Role;
import com.nhnacademy.hexashoppingmallservice.projection.member.MemberProjection;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.RatingRepository;
import com.nhnacademy.hexashoppingmallservice.service.book.LikeService;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberService;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberStatusService;
import com.nhnacademy.hexashoppingmallservice.service.member.RatingService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = MemberController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {
    @Getter
    @AllArgsConstructor
    static class TestMemberProjection implements MemberProjection {
        private String memberId;
        private String memberName;
        private String memberNumber;
        private String memberEmail;
        private LocalDate memberBirthAt;
        private LocalDateTime memberCreatedAt;
        private LocalDateTime memberLastLoginAt;
        private Role memberRole;
        private Rating rating;
        private MemberStatus memberStatus;
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private RatingService ratingService;

    @MockBean
    private MemberStatusService memberStatusService;

    @MockBean
    private LikeService likeService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private MemberStatusRepository memberStatusRepository;

    @MockBean
    private MemberRepository memberRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Rating rating;
    private MemberStatus memberStatus;

    @BeforeEach
    void setUp() {
        rating = Rating.of("Gold", 10);
        memberStatus = new MemberStatus("Active");
        rating.setRatingId(1L);
        memberStatus.setStatusId(1L);

        given(ratingRepository.findById(1L)).willReturn(Optional.of(rating));
        given(memberStatusRepository.findById(1L)).willReturn(Optional.of(memberStatus));
        given(ratingRepository.existsById(1L)).willReturn(true);
        given(memberStatusRepository.existsById(1L)).willReturn(true);
    }

    @Test
    void getMembers_withSearch() throws Exception {
        String search = "test";
        List<MemberProjection> mockMembers = List.of(
                new TestMemberProjection(
                        "test1", "John Doe", "01012345678", "john@test.com",
                        LocalDate.of(1990, 1, 1), LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 12, 15, 12, 30), Role.MEMBER, rating, memberStatus),
                new TestMemberProjection(
                        "test2", "Jane Doe", "01098765432", "jane@test.com",
                        LocalDate.of(1985, 5, 15), LocalDateTime.of(2023, 5, 10, 8, 0),
                        LocalDateTime.of(2024, 12, 15, 12, 30), Role.ADMIN, rating, memberStatus)
        );

        given(memberService.searchMembersById(any(Pageable.class), eq(search))).willReturn(mockMembers);

        mockMvc.perform(get("/api/members")
                        .param("page", "0")
                        .param("search", search)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockMembers.size()))
                .andExpect(jsonPath("$[0].memberId").value("test1"))
                .andExpect(jsonPath("$[0].memberName").value("John Doe"))
                .andDo(document("get-members-with-search",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("search").description("검색어 (선택)").optional()
                        ),
                        responseFields(
                                fieldWithPath("[].memberId").description("회원 ID"),
                                fieldWithPath("[].memberName").description("회원 이름"),
                                fieldWithPath("[].memberNumber").description("회원 전화번호"),
                                fieldWithPath("[].memberEmail").description("회원 이메일").optional(),
                                fieldWithPath("[].memberBirthAt").description("회원 생년월일").optional(),
                                fieldWithPath("[].memberCreatedAt").description("회원 생성일").optional(),
                                fieldWithPath("[].memberLastLoginAt").description("회원 마지막 로그인 시간").optional(),
                                fieldWithPath("[].memberRole").description("회원 권한").optional(),
                                // Nested fields for rating
                                fieldWithPath("[].rating.ratingId").description("회원 등급 ID"),
                                fieldWithPath("[].rating.ratingName").description("회원 등급 이름"),
                                fieldWithPath("[].rating.ratingPercent").description("회원 등급 퍼센트"),
                                // Nested fields for memberStatus
                                fieldWithPath("[].memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("[].memberStatus.statusName").description("회원 상태 이름")
                        )
                ));

        verify(memberService).searchMembersById(any(Pageable.class), eq(search));
    }


    @Test
    void getMembers_withoutSearch() throws Exception {
        List<MemberProjection> mockMembers = List.of(
                new TestMemberProjection(
                        "test1", "John Doe", "01012345678", "john@test.com",
                        LocalDate.of(1990, 1, 1), LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 12, 15, 12, 30), Role.MEMBER, rating, memberStatus),
                new TestMemberProjection(
                        "test2", "Jane Doe", "01098765432", "jane@test.com",
                        LocalDate.of(1985, 5, 15), LocalDateTime.of(2023, 5, 10, 8, 0),
                        LocalDateTime.of(2024, 12, 15, 12, 30), Role.ADMIN, rating, memberStatus)
        );

        // Mock the service call without search
        given(memberService.getMembers(any(Pageable.class), eq(null))).willReturn(mockMembers);

        mockMvc.perform(get("/api/members")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockMembers.size()))
                .andExpect(jsonPath("$[0].memberId").value("test1"))
                .andExpect(jsonPath("$[1].memberId").value("test2"));

        verify(memberService).getMembers(any(Pageable.class), eq(null));
    }

    @Test
    void getMember_success() throws Exception {
        // Mock Member data
        Rating rating = Rating.of("Gold", 10);
        rating.setRatingId(1L);
        MemberStatus status = new MemberStatus("Active");
        status.setStatusId(1L);

        Member mockMember = Member.of(
                "test1",
                "password123",
                "John Doe",
                "01012345678",
                "john.doe@test.com",
                LocalDate.of(1990, 1, 1),
                rating,
                status
        );

        given(memberService.getMember("test1")).willReturn(mockMember);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}", "test1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("test1"))
                .andExpect(jsonPath("$.memberName").value("John Doe"))
                .andExpect(jsonPath("$.memberRole").value("MEMBER"))
                .andExpect(jsonPath("$.rating.ratingName").value("Gold"))
                .andExpect(jsonPath("$.memberStatus.statusName").value("Active"))
                .andDo(document("get-member",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("조회할 회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호").optional(),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberEmail").description("회원 이메일"),
                                fieldWithPath("memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("memberCreatedAt").description("회원 가입일"),
                                fieldWithPath("memberLastLoginAt").description("회원 마지막 로그인 시간"),
                                fieldWithPath("memberRole").description("회원 권한"),
                                fieldWithPath("rating.ratingId").description("회원 등급 ID"),
                                fieldWithPath("rating.ratingName").description("회원 등급 이름"),
                                fieldWithPath("rating.ratingPercent").description("회원 등급 퍼센트"),
                                fieldWithPath("memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("memberStatus.statusName").description("회원 상태 이름")
                        )
                ));

        verify(memberService).getMember("test1");
    }

    /**
     * Mock Member 객체 생성 메서드
     */
    private Member createMockMember(String id, String name, String number, LocalDate birthDate, Rating rating,
                                    MemberStatus status) {
        return Member.of(
                id,
                "password",
                name,
                number,
                "test@test.com",
                birthDate,
                rating,
                status
        );
    }

    @Test
    void createMember() throws Exception {
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "test1",
                "password123",
                "John Doe",
                "01012345678",
                "john.doe@test.com",
                LocalDate.of(1990, 1, 1),
                null,
                "1",
                "1"
        );

        Member createdMember = createMockMember(
                "test1",
                "John Doe",
                "01012345678",
                LocalDate.of(1990, 1, 1),
                rating,
                memberStatus
        );

        given(memberService.createMember(any(MemberRequestDTO.class))).willReturn(createdMember);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value("test1"))
                .andExpect(jsonPath("$.memberName").value("John Doe"))
                .andDo(document("create-member",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호"),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberEmail").description("회원 이메일"),
                                fieldWithPath("memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("memberLastLoginAt").description("회원 마지막 로그인 일시").optional(),
                                fieldWithPath("ratingId").description("회원 등급 ID"),
                                fieldWithPath("statusId").description("회원 상태 ID")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호"),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberEmail").description("회원 이메일"),
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

    @Test
    void updateMember() throws Exception {
        // Mock request DTO
        MemberRequestDTO requestDTO = new MemberRequestDTO(
                "test1",
                "newPassword123",
                "John Updated",
                "01098765432",
                "updated@test.com",
                LocalDate.of(1990, 1, 1),
                LocalDateTime.now(),
                "2",
                "2"
        );

        // Mock updated Member
        Rating updatedRating = Rating.of("Platinum", 20);
        updatedRating.setRatingId(2L);
        MemberStatus updatedStatus = MemberStatus.builder().statusName("Inactive").build();
        updatedStatus.setStatusId(2L);

        Member updatedMember = Member.of(
                "test1",
                "newPassword123",
                "John Updated",
                "01098765432",
                "updated@test.com",
                LocalDate.of(1990, 1, 1),
                updatedRating,
                updatedStatus
        );

        // Mock service response
        given(memberService.updateMember(anyString(), any(MemberUpdateDTO.class))).willReturn(updatedMember);

        // Perform PATCH request
        mockMvc.perform(put("/api/members/{memberId}", "test1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("test1"))
                .andExpect(jsonPath("$.memberName").value("John Updated"))
                .andExpect(jsonPath("$.memberNumber").value("01098765432"))
                .andExpect(jsonPath("$.memberEmail").value("updated@test.com"))
                .andExpect(jsonPath("$.rating.ratingName").value("Platinum"))
                .andExpect(jsonPath("$.memberStatus.statusName").value("Inactive"))
                .andDo(document("update-member",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호"),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberEmail").description("회원 이메일").optional(),
                                fieldWithPath("memberBirthAt").description("회원 생년월일").optional(),
                                fieldWithPath("memberLastLoginAt").description("회원 마지막 로그인 일시").optional(),
                                fieldWithPath("ratingId").description("회원 등급 ID"),
                                fieldWithPath("statusId").description("회원 상태 ID")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("회원 ID"),
                                fieldWithPath("memberPassword").description("회원 비밀번호"),
                                fieldWithPath("memberName").description("회원 이름"),
                                fieldWithPath("memberNumber").description("회원 전화번호"),
                                fieldWithPath("memberEmail").description("회원 이메일"),
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

}

