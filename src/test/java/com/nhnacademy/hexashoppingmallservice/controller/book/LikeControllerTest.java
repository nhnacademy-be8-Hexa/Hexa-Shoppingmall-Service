package com.nhnacademy.hexashoppingmallservice.controller.book;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nhnacademy.hexashoppingmallservice.service.book.LikeService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = LikeController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @MockBean
    private JwtUtils jwtUtils;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLike() throws Exception {
        Long bookId = 1L;
        String memberId = "123";

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/likes")
                        .queryParam("bookId", String.valueOf(bookId))
                        .queryParam("memberId", memberId)
                        .header("Authorization", "Bearer dummy-token")
                )
                .andExpect(status().isCreated())
                .andDo(document("create-like",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        queryParameters(
                                parameterWithName("bookId").description("좋아요를 추가할 책의 ID"),
                                parameterWithName("memberId").description("좋아요를 추가할 회원의 ID")
                        )
                ));
    }
    
    @Test
    void getLikeCount() throws Exception {
        Long bookId = 1L;
        Long likeCount = 10L;

        given(likeService.sumLikes(bookId)).willReturn(likeCount);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/books/{bookId}/likes", bookId)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(likeCount)))
                .andDo(document("get-like-count",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        ),
                        responseBody()
                ));
    }

    @Test
    void toggleLike() throws Exception {
        Long bookId = 1L;
        String memberId = "123";


        // mock 호출
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/likes/toggle")
                        .queryParam("bookId", String.valueOf(bookId))
                        .queryParam("memberId", memberId)
                        .header("Authorization", "Bearer dummy-token")
                )
                .andExpect(status().isOk())
                .andDo(document("toggle-like",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        queryParameters(
                                parameterWithName("bookId").description("좋아요를 토글할 책의 ID"),
                                parameterWithName("memberId").description("좋아요를 토글할 회원의 ID")
                        )
                ));

        // likeService.toggleLike(bookId, memberId) 호출 검증
        verify(likeService).toggleLike(bookId, memberId);
    }

    @Test
    void hasLiked() throws Exception {
        Long bookId = 1L;
        String memberId = "123";
        Boolean hasLiked = true;

        // 모킹: likeService.hasLiked(bookId, memberId)
        given(likeService.hasLiked(bookId, memberId)).willReturn(hasLiked);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/likes/status")
                        .queryParam("bookId", String.valueOf(bookId))
                        .queryParam("memberId", memberId)
                        .accept("application/json")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(hasLiked)))
                .andDo(document("has-liked",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("bookId").description("도서 ID"),
                                parameterWithName("memberId").description("회원 ID")
                        ),
                        responseBody()
                ));
    }

}