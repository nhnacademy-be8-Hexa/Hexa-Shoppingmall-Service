package com.nhnacademy.hexashoppingmallservice.controller.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.tag.TagRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.service.tag.TagService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = TagController.class)
@AutoConfigureRestDocs
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Mock JwtUtils to always validate tokens successfully
        given(jwtUtils.getTokenFromRequest(any(HttpServletRequest.class))).willReturn("mock-token");
        given(jwtUtils.validateToken("mock-token")).willReturn(true);
        given(jwtUtils.getRoleFromToken("mock-token")).willReturn("ADMIN");
    }

    @Test
    void createTag() throws Exception {
        TagRequestDTO requestDTO = new TagRequestDTO("Science");

        mockMvc.perform(post("/api/admin/tags")
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andDo(document("create-tag",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("tagName").description("생성할 태그 이름")
                        )
                ));

        verify(tagService).createTag(any(TagRequestDTO.class));
    }

    @Test
    void updateTag() throws Exception {
        TagRequestDTO requestDTO = new TagRequestDTO("Updated Tag");

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/tags/{tagId}", 1L)
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andDo(document("update-tag",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("tagId").description("수정할 태그 ID")
                        ),
                        requestFields(
                                fieldWithPath("tagName").description("수정할 태그 이름")
                        )
                ));

        verify(tagService).updateTag(eq(1L), any(TagRequestDTO.class));
    }

    @Test
    void getAllTags() throws Exception {
        List<Tag> mockTags = List.of(
                Tag.of("Science"),
                Tag.of("Technology")
        );

        given(tagService.getAllTags()).willReturn(mockTags);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tags")
                        .header("Authorization", "Bearer mock-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-all-tags",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].tagId").description("태그 ID"),
                                fieldWithPath("[].tagName").description("태그 이름")
                        )
                ));

        verify(tagService).getAllTags();
    }

    @Test
    void deleteTag() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/tags/{tagId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("delete-tag",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("tagId").description("삭제할 태그 ID")
                        )
                ));

        verify(tagService).deleteTag(eq(1L));
    }


}