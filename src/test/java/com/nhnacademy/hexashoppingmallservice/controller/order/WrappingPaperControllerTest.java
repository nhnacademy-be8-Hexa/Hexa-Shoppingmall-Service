package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.order.WrappingPaperRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.service.order.WrappingPaperService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = WrappingPaperController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class WrappingPaperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WrappingPaperService wrappingPaperService;


    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    @DisplayName("Get all wrapping papers - Success")
    void testGetAllWrappingPapers_ReturnsWrappingPaperList() throws Exception {
        WrappingPaper wrappingPaper1 = WrappingPaper.builder()
                .wrappingPaperName("Premium Paper")
                .wrappingPaperPrice(100)
                .build();

        Field wrappingPaperField1 = wrappingPaper1.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperField1.setAccessible(true);
        wrappingPaperField1.set(wrappingPaper1, 1L);


        WrappingPaper wrappingPaper2 = WrappingPaper.builder()
                .wrappingPaperName("Standard Paper")
                .wrappingPaperPrice(50)
                .build();

        Field wrappingPaperField2 = wrappingPaper2.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperField2.setAccessible(true);
        wrappingPaperField2.set(wrappingPaper2, 2L);

        when(wrappingPaperService.getAllWrappingPaper())
                .thenReturn(List.of(wrappingPaper1, wrappingPaper2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/wrappingPaper")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].wrappingPaperName").value("Premium Paper"))
                .andExpect(jsonPath("$[0].wrappingPaperPrice").value(100))
                .andExpect(jsonPath("$[1].wrappingPaperName").value("Standard Paper"))
                .andExpect(jsonPath("$[1].wrappingPaperPrice").value(50))
                .andDo(document("get-all-wrapping-papers",
                        preprocessRequest(), preprocessResponse(),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("[].wrappingPaperId").description("포장지 번호"),
                                PayloadDocumentation.fieldWithPath("[].wrappingPaperName").description("포장지 이름"),
                                PayloadDocumentation.fieldWithPath("[].wrappingPaperPrice").description("포장지 가격")
                        )
                ));
    }


    @Test
    @DisplayName("Create wrapping paper - Success")
    void testCreateWrappingPaper_ReturnsCreatedWrappingPaper() throws Exception {
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO("Premium Paper", 100);

        WrappingPaper createdWrappingPaper = WrappingPaper.builder()
                .wrappingPaperName("Premium Paper")
                .wrappingPaperPrice(100)
                .build();

        Field wrappingPaperField = createdWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperField.setAccessible(true);
        wrappingPaperField.set(createdWrappingPaper, 1L);



        when(wrappingPaperService.createWrappingPaper(any(WrappingPaperRequestDTO.class)))
                .thenReturn(createdWrappingPaper);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/wrappingPaper")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wrappingPaperName").value("Premium Paper"))
                .andExpect(jsonPath("$.wrappingPaperPrice").value(100))
                .andDo(document("create-wrapping-paper",
                        preprocessRequest(), preprocessResponse(),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("wrappingPaperName").description("포장지 이름"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperPrice").description("포장지 가격")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("wrappingPaperId").description("포장지 번호"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperName").description("포장지 이름"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperPrice").description("포장지 가격")
                        )
                ));
    }


    @Test
    @DisplayName("Get wrapping paper by ID - Success")
    void testGetWrappingPaper_ReturnsWrappingPaper_Success() throws Exception {
        WrappingPaper wrappingPaper = WrappingPaper.builder()
                .wrappingPaperName("Premium Paper")
                .wrappingPaperPrice(100)
                .build();

        Field wrappingPaperField = wrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperField.setAccessible(true);
        wrappingPaperField.set(wrappingPaper, 1L);

        when(wrappingPaperService.getWrappingPaper(1L)).thenReturn(wrappingPaper);

        mockMvc.perform(get("/api/wrappingPaper/{wrappingPaperId}",1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wrappingPaperName").value("Premium Paper"))
                .andExpect(jsonPath("$.wrappingPaperPrice").value(100))
                .andDo(document("get-wrapping-paper",
                        preprocessRequest(), preprocessResponse(),
                        pathParameters(
                                parameterWithName("wrappingPaperId").description("포장지 번호")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("wrappingPaperId").description("포장지 번호"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperName").description("포장지 이름"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperPrice").description("포장지 가격")
                        )
                ));
    }


    @Test
    @DisplayName("Update wrapping paper - Success")
    void testUpdateWrappingPaper_ReturnsUpdatedWrappingPaper_Success() throws Exception {
        WrappingPaperRequestDTO requestDTO = new WrappingPaperRequestDTO("Updated Paper", 120);

        WrappingPaper updatedWrappingPaper = WrappingPaper.builder()
                .wrappingPaperName("Updated Paper")
                .wrappingPaperPrice(120)
                .build();

        Field wrappingPaperField = updatedWrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperField.setAccessible(true);
        wrappingPaperField.set(updatedWrappingPaper, 1L);

        when(wrappingPaperService.updateWrappingPaper(Mockito.eq(1L), any(WrappingPaperRequestDTO.class)))
                .thenReturn(updatedWrappingPaper);

        mockMvc.perform(put("/api/wrappingPaper/{wrappingPaperId}",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wrappingPaperName").value("Updated Paper"))
                .andExpect(jsonPath("$.wrappingPaperPrice").value(120))
                .andDo(document("update-wrapping-paper",
                        preprocessRequest(), preprocessResponse(),
                        pathParameters(
                                parameterWithName("wrappingPaperId").description("포장지 번호")
                        ),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("wrappingPaperName").description("포장지 이름"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperPrice").description("포장지 가격")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("wrappingPaperId").description("포장지 번호"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperName").description("포장지 이름"),
                                PayloadDocumentation.fieldWithPath("wrappingPaperPrice").description("포장지 가격")
                        )
                ));
    }


    @Test
    @DisplayName("Delete wrapping paper - Success")
    void testDeleteWrappingPaper_ReturnsNoContent_Success() throws Exception {
        Mockito.doNothing().when(wrappingPaperService).deleteWrappingPaper(1L);

        mockMvc.perform(delete("/api/wrappingPaper/{wrappingPaperId}",1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("delete-wrapping-paper",
                        preprocessRequest(), preprocessResponse(),
                        pathParameters(
                                parameterWithName("wrappingPaperId").description("포장지 번호")
                        )
                ));
    }

}
