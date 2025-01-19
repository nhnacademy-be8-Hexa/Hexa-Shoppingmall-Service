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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        given(tagService.getAllTags(any(Pageable.class))).willReturn(mockTags);

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

        verify(tagService).getAllTags(any(Pageable.class));
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


    @Test
    void getTotalTags() throws Exception {
        long mockTotalTags = 5L;  // 예시로 5개의 태그가 있다고 가정

        // mock 서비스 응답 설정
        given(tagService.getTotal()).willReturn(mockTotalTags);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/tags/count")
                        .header("Authorization", "Bearer mock-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    // 응답 본문에 '5'가 포함되어 있는지 검증
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).isEqualTo("5");
                })
                .andDo(document("get-total-tags",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseBody()
                ));

        verify(tagService).getTotal();
    }

    @Test
    void getTagById() throws Exception {
        // 예시로 ID가 1인 태그를 mock으로 설정
        Tag mockTag = Tag.of("Science");
        Field tagIdField = mockTag.getClass().getDeclaredField("tagId");
        tagIdField.setAccessible(true);
        tagIdField.set(mockTag, 1L);

        given(tagService.findTagById(1L)).willReturn(mockTag);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/tags/{tagId}", 1L)
                        .header("Authorization", "Bearer mock-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    // 응답 본문에 해당 태그의 이름이 포함되어 있는지 검증
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).contains("\"tagName\":\"Science\"");
                })
                .andDo(document("get-tag-by-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("tagId").description("조회할 태그 ID")
                        ),
                        responseFields(
                                fieldWithPath("tagId").description("태그 ID"),
                                fieldWithPath("tagName").description("태그 이름")
                        )
                ));

        verify(tagService).findTagById(eq(1L));
    }



}
