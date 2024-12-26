package com.nhnacademy.hexashoppingmallservice.controller.member.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
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
import com.nhnacademy.hexashoppingmallservice.controller.book.PublisherController;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.service.book.PublisherService;
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
@WebMvcTest(controllers = PublisherController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublisherService publisherService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Publisher publisher;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        publisher = Publisher.of("Test Publisher");
        Field publisherIdField = publisher.getClass().getDeclaredField("publisherId");
        publisherIdField.setAccessible(true);
        publisherIdField.set(publisher, 1L);
    }

    @Test
    void getPublishers() throws Exception {
        given(publisherService.getAllPublisher(any())).willReturn(List.of(publisher));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/publishers")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publisherId").value(1L))
                .andExpect(jsonPath("$[0].publisherName").value("Test Publisher"))
                .andDo(document("get-publishers",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].publisherId").description("출판사 ID"),
                                fieldWithPath("[].publisherName").description("출판사 이름")
                        )
                ));
    }

    @Test
    void createPublisher() throws Exception {
        given(publisherService.createPublisher(any(Publisher.class))).willReturn(publisher);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/publishers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(publisher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publisherId").value(1L))
                .andExpect(jsonPath("$.publisherName").value("Test Publisher"))
                .andDo(document("create-publisher",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("publisherId").description("출판사 ID"),
                                fieldWithPath("publisherName").description("출판사 이름")
                        ),
                        responseFields(
                                fieldWithPath("publisherId").description("출판사 ID"),
                                fieldWithPath("publisherName").description("출판사 이름")
                        )
                ));
    }

    @Test
    void getPublisher() throws Exception {
        given(publisherService.getPublisher(anyLong())).willReturn(publisher);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/publishers/{publisherId}", 1L)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publisherId").value(1L))
                .andExpect(jsonPath("$.publisherName").value("Test Publisher"))
                .andDo(document("get-publisher",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("publisherId").description("출판사 ID")
                        ),
                        responseFields(
                                fieldWithPath("publisherId").description("출판사 ID"),
                                fieldWithPath("publisherName").description("출판사 이름")
                        )
                ));
    }
}
