package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsReasonRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.ReturnsReason;
import com.nhnacademy.hexashoppingmallservice.service.order.ReturnsReasonService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(ReturnsReasonController.class)
public class ReturnsReasonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReturnsReasonService returnsReasonService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private ReturnsReasonRequestDTO requestDTO;
    private ReturnsReason responseEntity;

    @BeforeEach
    void setUp() throws Exception {
        requestDTO = new ReturnsReasonRequestDTO("Reason for return");
        responseEntity = ReturnsReason.of ("Reason for return");

        Field returnsReasonIdField = responseEntity.getClass().getDeclaredField("returnsReasonId");
        returnsReasonIdField.setAccessible(true);
        returnsReasonIdField.set(responseEntity, 1L);
    }

    @Test
    void testCreateReturnsReason() throws Exception {
        // Given
        when(returnsReasonService.createReturnsReason(any(ReturnsReasonRequestDTO.class))).thenReturn(responseEntity);
        doNothing().when(jwtUtils).ensureAdmin(any());

        // When
        ResultActions result = mockMvc.perform(post("/api/returnsReason")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReasonId").value(1))
                .andExpect(jsonPath("$.returnsReason").value("Reason for return"))
                .andDo(document("returnsReason/create",
                        requestFields(
                                fieldWithPath("returnsReason").description("반품사유")
                        ),
                        responseFields(
                                fieldWithPath("returnsReasonId").description("반품사유 ID"),
                                fieldWithPath("returnsReason").description("반품사유")
                        )));
    }

    @Test
    void testGetAllReturnsReasons() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(returnsReasonService.getReturnsReasons(pageable)).thenReturn(new PageImpl<>(List.of(responseEntity)).getContent());

        // When
        ResultActions result = mockMvc.perform(get("/api/returnsReason")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].returnsReasonId").value(1))
                .andExpect(jsonPath("$[0].returnsReason").value("Reason for return"))
                .andDo(document("returnsReason/get-all",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("한 페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[].returnsReasonId").description("반품사유 ID"),
                                fieldWithPath("[].returnsReason").description("반품사유")
                        )));
    }

    @Test
    void testGetReturnsReasonById() throws Exception {
        // Given
        when(returnsReasonService.getReturnsReason(1L)).thenReturn(responseEntity);

        // When
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/returnsReason/{returnsReasonId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReasonId").value(1))
                .andExpect(jsonPath("$.returnsReason").value("Reason for return"))
                .andDo(document("returnsReason/get-by-id",
                        pathParameters(
                                parameterWithName("returnsReasonId").description("반품사유 ID")
                        ),
                        responseFields(
                                fieldWithPath("returnsReasonId").description("반품사유 ID"),
                                fieldWithPath("returnsReason").description("반품사유")
                        )));
    }

    @Test
    void testUpdateReturnsReason() throws Exception {
        // Given
        when(returnsReasonService.updateReturnsReason(any(), any())).thenReturn(responseEntity);
        doNothing().when(jwtUtils).ensureAdmin(any());

        // When
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/returnsReason/{returnsReasonId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReasonId").value(1))
                .andExpect(jsonPath("$.returnsReason").value("Reason for return"))
                .andDo(document("returnsReason/update",
                        pathParameters(
                                parameterWithName("returnsReasonId").description("업데이트 할 반품 사유 ID")
                        ),
                        requestFields(
                                fieldWithPath("returnsReason").description("업데이트 할 반품 사유")
                        ),
                        responseFields(
                                fieldWithPath("returnsReasonId").description("업데이트 한 반품사유 ID"),
                                fieldWithPath("returnsReason").description("업데이트 한 반품사유")
                        )));
    }

    @Test
    void testDeleteReturnsReason() throws Exception {
        // Given
        doNothing().when(jwtUtils).ensureAdmin(any());
        doNothing().when(returnsReasonService).deleteReturnsReason(1L);

        // When
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/returnsReason/{returnsReasonId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andDo(document("returnsReason/delete",
                        pathParameters(
                                parameterWithName("returnsReasonId").description("지울 반품 사유 ID")
                        )));
    }
}