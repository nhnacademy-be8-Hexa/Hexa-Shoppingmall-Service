package com.nhnacademy.hexashoppingmallservice.controller.elasticsearch;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nhnacademy.hexashoppingmallservice.document.BookStatus;
import com.nhnacademy.hexashoppingmallservice.document.Publisher;
import com.nhnacademy.hexashoppingmallservice.dto.book.SearchBookDTO;
import com.nhnacademy.hexashoppingmallservice.service.elasticsearch.ElasticSearchService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ElasticSearchController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ElasticSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElasticSearchService elasticSearchService;

    private SearchBookDTO searchBookDTO;

    @BeforeEach
    void setUp() {
        Publisher publisher = Publisher.of(1L, "Test Publisher");
        BookStatus bookStatus = BookStatus.of(1L, "Test Book Status");
        searchBookDTO = new SearchBookDTO(1L, "Test Book", "Test Description", publisher, bookStatus, 1234567890L,
                "2022-01-01", 2000, 1500, true, 100, 10, 50L);
    }

    @Test
    void searchBooks() throws Exception {
        // mock 데이터 반환
        given(elasticSearchService.searchBooks(anyString(), any(Pageable.class)))
                .willReturn(List.of(searchBookDTO));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/search")
                        .param("search", "Test Book")
                        .accept("application/json")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(1L))
                .andExpect(jsonPath("$[0].bookTitle").value("Test Book"))
                .andExpect(jsonPath("$[0].bookDescription").value("Test Description"))
                .andExpect(jsonPath("$[0].publisher.publisherId").value(1L))
                .andExpect(jsonPath("$[0].publisher.publisherName").value("Test Publisher"))
                .andExpect(jsonPath("$[0].bookStatus.bookStatusId").value(1L))
                .andExpect(jsonPath("$[0].bookStatus.bookStatus").value("Test Book Status"))
                .andExpect(jsonPath("$[0].bookIsbn").value(1234567890L))
                .andExpect(jsonPath("$[0].bookPubDate").value("2022-01-01"))
                .andExpect(jsonPath("$[0].bookOriginPrice").value(2000))
                .andExpect(jsonPath("$[0].bookPrice").value(1500))
                .andExpect(jsonPath("$[0].bookWrappable").value(true))
                .andExpect(jsonPath("$[0].bookView").value(100))
                .andExpect(jsonPath("$[0].bookAmount").value(10))
                .andExpect(jsonPath("$[0].bookSellCount").value(50L))
                .andDo(document("search-books",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("search").description("검색어"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[].bookId").description("도서 ID"),
                                fieldWithPath("[].bookTitle").description("도서 제목"),
                                fieldWithPath("[].bookDescription").description("도서 설명"),
                                fieldWithPath("[].publisher.publisherId").description("출판사 ID"),
                                fieldWithPath("[].publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("[].bookStatus.bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("[].bookStatus.bookStatus").description("도서 상태 이름"),
                                fieldWithPath("[].bookIsbn").description("도서 ISBN"),
                                fieldWithPath("[].bookPubDate").description("도서 출판일"),
                                fieldWithPath("[].bookOriginPrice").description("도서 정가"),
                                fieldWithPath("[].bookPrice").description("도서 판매가"),
                                fieldWithPath("[].bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("[].bookView").description("도서 조회 수"),
                                fieldWithPath("[].bookAmount").description("도서 재고 수"),
                                fieldWithPath("[].bookSellCount").description("도서 판매 수")
                        )
                ));
    }

    @Test
    void getTotalBooks() throws Exception {
        given(elasticSearchService.getTotal(anyString())).willReturn(100L);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/search/total")
                        .param("search", "Test Book")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100L))
                .andDo(document("get-searched-book-count",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("search").description("검색어")
                        ),
                        responseBody()
                ));
    }
}
