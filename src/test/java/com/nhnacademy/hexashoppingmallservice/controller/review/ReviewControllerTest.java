package com.nhnacademy.hexashoppingmallservice.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.book.ReviewRequestDTO;
import com.nhnacademy.hexashoppingmallservice.projection.review.ReviewProjection;
import com.nhnacademy.hexashoppingmallservice.service.review.ReviewService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private String memberId;

    @BeforeEach
    void setUp() {
        memberId = "member123";
        validToken = "Bearer valid.jwt.token";
    }

    /**
     * ReviewProjection.MemberProjection 객체를 생성하는 헬퍼 메서드.
     */
    private ReviewProjection.MemberProjection createMemberProjection(String memberId) {
        return new ReviewProjection.MemberProjection() {
            @Override
            public String getMemberId() {
                return memberId;
            }
        };
    }

    /**
     * ReviewProjection 객체를 생성하는 헬퍼 메서드.
     */
    private ReviewProjection createReviewProjection(Long reviewId, String reviewContent, BigDecimal reviewRating, ReviewProjection.MemberProjection memberProjection) {
        return new ReviewProjection() {
            @Override
            public Long getReviewId() {
                return reviewId;
            }

            @Override
            public String getReviewContent() {
                return reviewContent;
            }

            @Override
            public BigDecimal getReviewRating() {
                return reviewRating;
            }

            @Override
            public MemberProjection getMember() {
                return memberProjection;
            }

            @Override
            public Boolean isReviewIsblocked() {
                return false;
            }
        };
    }

    /**
     * 새로운 리뷰를 성공적으로 생성하는 테스트.
     */
    @Test
    @DisplayName("POST /api/members/{memberId}/books/{bookId}/reviews - 성공적으로 리뷰 생성")
    void createReview_Success() throws Exception {
        // Arrange
        Long bookId = 1L;
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("Great book!", new BigDecimal("5"));

        // Act & Assert
        mockMvc.perform(post("/api/members/{memberId}/books/{bookId}/reviews", memberId, bookId)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isCreated())
                .andDo(document("create-review",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("리뷰를 작성할 회원의 ID"),
                                parameterWithName("bookId").description("리뷰를 작성할 책의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        requestFields(
                                fieldWithPath("reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("reviewRating").type(JsonFieldType.NUMBER).description("리뷰 평점")
                        )
                ));
    }

    /**
     * 특정 회원의 리뷰 목록을 성공적으로 조회하는 테스트.
     */
    @Test
    @DisplayName("GET /api/members/{memberId}/reviews - 특정 회원의 리뷰 목록 조회")
    void getReviewsFromMember_Success() throws Exception {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        ReviewProjection.MemberProjection memberProjection = createMemberProjection(memberId);

        ReviewProjection review1 = createReviewProjection(1L, "Great book!", new BigDecimal("5"), memberProjection);
        ReviewProjection review2 = createReviewProjection(2L, "Not bad.", new BigDecimal("3"), memberProjection);

        List<ReviewProjection> reviews = Arrays.asList(review1, review2);

        // Mock Review Service with all arguments as matchers
        Mockito.when(reviewService.getReviewsFromMember(pageable, memberId)).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/members/{memberId}/reviews", memberId)
                        .header("Authorization", validToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(document("get-reviews-from-member",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("리뷰를 조회할 회원의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[].reviewId").type(JsonFieldType.NUMBER).description("리뷰의 ID"),
                                fieldWithPath("[].reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("[].reviewRating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("리뷰를 작성한 회원 정보"),
                                fieldWithPath("[].reviewIsblocked").type(JsonFieldType.BOOLEAN).description("리뷰의 차단 여부"),
                                fieldWithPath("[].member.memberId").type(JsonFieldType.STRING).description("회원의 ID")
                        )
                ));
    }

    /**
     * 특정 리뷰를 성공적으로 수정하는 테스트.
     */
    @Test
    @DisplayName("PUT /api/reviews/{reviewId} - 성공적으로 리뷰 수정")
    void updateReview_Success() throws Exception {
        // Arrange
        Long reviewId = 1L;
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("Updated review content.", new BigDecimal("4"));

        // Mock Review Service
        Mockito.doNothing().when(reviewService).updateReview(reviewRequestDTO, reviewId);

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDTO)))
                .andExpect(status().isNoContent())
                .andDo(document("update-review",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("수정할 리뷰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        requestFields(
                                fieldWithPath("reviewContent").type(JsonFieldType.STRING).description("수정된 리뷰 내용"),
                                fieldWithPath("reviewRating").type(JsonFieldType.NUMBER).description("수정된 리뷰 평점")
                        )
                ));
    }

    /**
     * 특정 책의 리뷰 목록을 성공적으로 조회하는 테스트.
     */
    @Test
    @DisplayName("GET /api/books/{bookId}/reviews - 특정 책의 리뷰 목록 조회")
    void getReviewsFromBook_Success() throws Exception {
        // Arrange
        Long bookId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        ReviewProjection.MemberProjection memberProjection = createMemberProjection(memberId);

        ReviewProjection review1 = createReviewProjection(1L, "Great book!", new BigDecimal("5"), memberProjection);
        ReviewProjection review2 = createReviewProjection(2L, "Not bad.", new BigDecimal("3"), memberProjection);

        List<ReviewProjection> reviews = Arrays.asList(review1, review2);

        // Mock Review Service with all arguments as matchers
        Mockito.when(reviewService.getReviewsFromBook(pageable, bookId))
                .thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/books/{bookId}/reviews", bookId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(document("get-reviews-from-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("리뷰를 조회할 책의 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[].reviewId").type(JsonFieldType.NUMBER).description("리뷰의 ID"),
                                fieldWithPath("[].reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("[].reviewRating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("리뷰를 작성한 회원 정보"),
                                fieldWithPath("[].reviewIsblocked").type(JsonFieldType.BOOLEAN).description("리뷰의 차단 여부"),
                                fieldWithPath("[].member.memberId").type(JsonFieldType.STRING).description("회원의 ID")
                        )
                ));
    }

    /**
     * 특정 리뷰를 성공적으로 삭제하는 테스트.
     */
    @Test
    @DisplayName("DELETE /api/reviews/{reviewId} - 성공적으로 리뷰 삭제")
    void deleteReview_Success() throws Exception {
        // Arrange
        Long reviewId = 1L;

        // Mock Review Service
        Mockito.doNothing().when(reviewService).deleteReview(eq(reviewId));

        // Act & Assert
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .header("Authorization", validToken))
                .andExpect(status().isNoContent())

                .andDo(document("delete-review",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("삭제할 리뷰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        )
                ));
    }

    /**
     * 관리자용 리뷰 목록을 성공적으로 조회하는 테스트.
     */
    @Test
    @DisplayName("GET /api/reviews - 관리자용 리뷰 목록 조회")
    void getReviewsFromAdmin_Success() throws Exception {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        ReviewProjection.MemberProjection memberProjection = createMemberProjection(memberId);

        ReviewProjection review1 = createReviewProjection(1L, "Great book!", new BigDecimal("5"), memberProjection);
        ReviewProjection review2 = createReviewProjection(2L, "Not bad.", new BigDecimal("3"), memberProjection);

        List<ReviewProjection> reviews = Arrays.asList(review1, review2);

        // Mock Review Service with all arguments as matchers
        Mockito.when(reviewService.getReviewsIsBlocked(pageable))
                .thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews")
                        .param("page", "0")
                        .param("size", "10")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(document("get-reviews-from-admin",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[].reviewId").type(JsonFieldType.NUMBER).description("리뷰의 ID"),
                                fieldWithPath("[].reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("[].reviewRating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("리뷰를 작성한 회원 정보"),
                                fieldWithPath("[].reviewIsblocked").type(JsonFieldType.BOOLEAN).description("리뷰의 차단 여부"),
                                fieldWithPath("[].member.memberId").type(JsonFieldType.STRING).description("회원의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        )
                ));
    }

    /**
     * 특정 리뷰의 차단 상태를 성공적으로 업데이트하는 테스트.
     */
    @Test
    @DisplayName("PUT /api/reviews/{reviewId}/block - 리뷰 차단 상태 업데이트")
    void updateReviewBlock_Success() throws Exception {
        // Arrange
        Long reviewId = 1L;
        boolean block = true;

        // Mock Review Service
        Mockito.doNothing().when(reviewService).updateBlocked(eq(reviewId), eq(block));

        // Act & Assert
        mockMvc.perform(put("/api/reviews/{reviewId}/block?block={block}", reviewId, block)
                        .header("Authorization", validToken))
                .andExpect(status().isNoContent())
                .andDo(document("update-review-block",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("차단 상태를 업데이트할 리뷰의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        queryParameters(
                                parameterWithName("block").description("리뷰 차단 상태 (true 또는 false)")
                        )
                ));
    }
}