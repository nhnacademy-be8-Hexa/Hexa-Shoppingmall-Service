package com.nhnacademy.hexashoppingmallservice.controller.book;

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.book.AuthorRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.service.book.AuthorService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import java.lang.reflect.Field;
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
@WebMvcTest(controllers = AuthorController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private AuthorRequestDTO authorRequestDTO;
    private Author author;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        authorRequestDTO = new AuthorRequestDTO(
                "Test Author", 1L);
        author = Author.of("Test Author");
        Field authorIdField = author.getClass().getDeclaredField("authorId");
        authorIdField.setAccessible(true);
        authorIdField.set(author, 1L);

    }

    @Test
    void createAuthor() throws Exception {

        given(authorService.createAuthor(any(String.class), any(Long.class))).willReturn(author);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/authors")
                        .header("Authorization", "Bearer dummy-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authorRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.authorName").value("Test Author"))
                .andDo(document("create-author",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        requestFields(
                                fieldWithPath("bookId").description("책 ID"),
                                fieldWithPath("authorName").description("저자 이름")
                        ),
                        responseFields(
                                fieldWithPath("authorId").description("저자 ID"),
                                fieldWithPath("authorName").description("저자 이름")
                        )
                ));
    }
}