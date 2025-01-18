package com.nhnacademy.hexashoppingmallservice.controller.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.document.BookStatus;
import com.nhnacademy.hexashoppingmallservice.dto.book.MemberUpdateDTO;
import com.nhnacademy.hexashoppingmallservice.dto.member.MemberRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
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

        // Mock 설정
        given(memberService.getMembers(any(Pageable.class), eq(search))).willReturn(mockMembers);

        mockMvc.perform(get("/api/members")
                        .param("page", "0")
                        .param("search", search)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockMembers.size()))
                .andExpect(jsonPath("$[0].memberId").value("test1"))
                .andExpect(jsonPath("$[1].memberId").value("test2"))
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

        verify(memberService).getMembers(any(Pageable.class), eq(search));
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

    @Test
    void getMemberCount_withSearch() throws Exception {
        // Mock 데이터: 검색어 "test"에 해당하는 회원 수
        String search = "test";
        long mockCount = 10L;

        // 관리자 권한을 가진 사용자의 JWT 토큰을 Mocking
        String mockJwtToken = "mock-jwt-token";

        doNothing().when(jwtUtils).ensureAdmin(any(HttpServletRequest.class));

        // Mock 서비스 호출
        given(memberService.countBySearch(eq(search))).willReturn(mockCount);

        // Perform GET request with search query
        mockMvc.perform(get("/api/members/count")
                        .param("search", search)
                        .header("Authorization", "Bearer " + mockJwtToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-member-count-with-search",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("search").description("검색어 (선택)").optional()
                        ),
                        responseBody()
                ));

        verify(memberService).countBySearch(eq(search));
    }

    @Test
    void getLikedBooks() throws Exception {
        String memberId = "test1";


         Publisher publisher;
         com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus bookStatus;
         com.nhnacademy.hexashoppingmallservice.entity.book.Book book;
         Author author;

        publisher = Publisher.of("Test Publisher");
        bookStatus = com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus.of("Test Book Status");
        author = Author.of("Test Author");

        Field publisherIdField = publisher.getClass().getDeclaredField("publisherId");
        publisherIdField.setAccessible(true);
        publisherIdField.set(publisher, 1L);

        Field bookStatusIdField = bookStatus.getClass().getDeclaredField("bookStatusId");
        bookStatusIdField.setAccessible(true);
        bookStatusIdField.set(bookStatus, 1L);

        Field authorIdField = author.getClass().getDeclaredField("authorId");
        authorIdField.setAccessible(true);
        authorIdField.set(author, 1L);


        List<Book> likedBooks = List.of(
                Book.of(
                        "Book Title",
                        "Book Description",
                        LocalDate.now(),
                        1234567890123L,
                        20000,
                        18000,
                        publisher,
                        bookStatus
                ),
                Book.of(
                        "Book Title2",
                        "Book Description2",
                        LocalDate.now(),
                        1234567890124L,
                        30000,
                        20000,
                        publisher,
                        bookStatus
                )
        );

        Field BookIdField1 = likedBooks.getFirst().getClass().getDeclaredField("bookId");
        BookIdField1.setAccessible(true);
        BookIdField1.set(likedBooks.getFirst(), 1L);

        Field BookIdField2 = likedBooks.getLast().getClass().getDeclaredField("bookId");
        BookIdField2.setAccessible(true);
        BookIdField2.set(likedBooks.getLast(), 2L);

        // Mock the service call
        given(likeService.getBooksLikedByMember(memberId)).willReturn(likedBooks);

        // Perform GET request
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/{memberId}/liked-books", memberId)
                        .header("Authorization", "Bearer mock-jwt-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(likedBooks.size()))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[1].bookId").value(2))
                .andDo(document("get-liked-books", // 문서화된 요청 이름
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), // 요청 및 응답 포맷
                        pathParameters(
                                parameterWithName("memberId").description("조회할 회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].bookId").description("도서 ID"),
                                fieldWithPath("[].bookTitle").description("도서 제목"),
                                fieldWithPath("[].bookDescription").description("도서 설명"),
                                fieldWithPath("[].bookPubDate").description("도서 출간일"),
                                fieldWithPath("[].bookIsbn").description("도서 ISBN"),
                                fieldWithPath("[].bookView").description("도서 페이지 조회수"),
                                fieldWithPath("[].bookAmount").description("도서 재고"),
                                fieldWithPath("[].bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("[].bookSellCount").description("도서 판매량"),
                                fieldWithPath("[].bookOriginPrice").description("도서 정가"),
                                fieldWithPath("[].bookPrice").description("도서 판매가"),
                                fieldWithPath("[].publisher.publisherId").description("출판사 ID"),
                                fieldWithPath("[].publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("[].bookStatus.bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("[].bookStatus.bookStatus").description("도서 상태")
                        )
                ));

        verify(likeService).getBooksLikedByMember(memberId);
    }



    @Test
    void loginMember() throws Exception {
        String memberId = "test1";

        // Mock the login action
        doNothing().when(memberService).login(memberId);

        // Perform PUT request
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/members/{memberId}/login", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("login-member",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("로그인할 회원 ID")
                        )
                ));

        verify(memberService).login(memberId);
    }




}

