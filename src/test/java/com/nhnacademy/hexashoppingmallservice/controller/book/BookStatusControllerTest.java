package com.nhnacademy.hexashoppingmallservice.controller.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.service.book.BookStatusService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = BookStatusController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class BookStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookStatusService bookStatusService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    private BookStatus bookStatus;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        bookStatus = BookStatus.of("Test Book Status");
        Field bookStatusIdField = bookStatus.getClass().getDeclaredField("bookStatusId");
        bookStatusIdField.setAccessible(true);
        bookStatusIdField.set(bookStatus, 1L);
    }

    @Test
    void getBookStatuses() throws Exception {
        given(bookStatusService.getAllBookStatus()).willReturn(List.of(bookStatus));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/bookStatuses")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookStatusId").value(1L))
                .andExpect(jsonPath("$[0].bookStatus").value("Test Book Status"))
                .andDo(document("get-book-statuses",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("[].bookStatus").description("도서 상태")
                        )
                ));
    }

    @Test
    void createBookStatus() throws Exception {
        given(bookStatusService.createBookStatus(any(BookStatus.class))).willReturn(bookStatus);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/bookStatuses")
                        .header("Authorization", "Bearer dummy-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookStatusId").value(1L))
                .andExpect(jsonPath("$.bookStatus").value("Test Book Status"))
                .andDo(document("create-book-status",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        requestFields(
                                fieldWithPath("bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("bookStatus").description("도서 상태")
                        ),
                        responseFields(
                                fieldWithPath("bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("bookStatus").description("도서 상태")
                        )
                ));
    }

    @Test
    void getBookStatus() throws Exception {
        given(bookStatusService.getBookStatus(anyLong())).willReturn(bookStatus);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/bookStatuses/{bookStatusId}", 1L)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookStatusId").value(1))
                .andExpect(jsonPath("$.bookStatus").value("Test Book Status"))
                .andDo(document("get-book-status",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookStatusId").description("도서 상태 ID")
                        ),
                        responseFields(
                                fieldWithPath("bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("bookStatus").description("도서 상태")
                        )
                ));
    }
}
