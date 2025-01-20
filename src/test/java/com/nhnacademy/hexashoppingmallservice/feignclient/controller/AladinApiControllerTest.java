package com.nhnacademy.hexashoppingmallservice.feignclient.controller;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.dto.AladinBookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.feignclient.service.AladinApiService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
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
@WebMvcTest(controllers = AladinApiController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class AladinApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AladinApiService aladinApiService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private AladinBookDTO aladinBookDTO;
    private AladinBookRequestDTO aladinBookRequestDTO;
    private BookStatus bookStatus;
    private Publisher publisher;
    private Book book;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        aladinBookDTO = new AladinBookDTO();
        aladinBookDTO.setTitle("Sample Book Title");
        aladinBookDTO.setAuthors(Arrays.asList("Author 1", "Author 2"));
        aladinBookDTO.setPriceSales(20000);
        aladinBookDTO.setPriceStandard(25000);
        aladinBookDTO.setPublisher("Sample Publisher");
        aladinBookDTO.setPubDate(LocalDate.of(2022, 1, 1));
        aladinBookDTO.setIsbn13(1234567890123L);
        aladinBookDTO.setDescription("This is a sample book description.");
        aladinBookDTO.setSalesPoint(1000L);
        aladinBookDTO.setCover("http://example.com/cover.jpg");

        aladinBookRequestDTO = new AladinBookRequestDTO(
                "Sample Book Title",
                Arrays.asList("Author 1", "Author 2"),
                20000,
                25000,
                "Sample Publisher",
                "1",
                LocalDate.of(2022, 1, 1),
                1234567890123L,
                "This is a sample book description.",
                true,
                100
        );

        bookStatus = BookStatus.of("Available");
        Field bookStatusIdField = bookStatus.getClass().getDeclaredField("bookStatusId");
        bookStatusIdField.setAccessible(true);
        bookStatusIdField.set(bookStatus, 1L);

        publisher = Publisher.of(aladinBookRequestDTO.getPublisher());
        Field publisherIdField = publisher.getClass().getDeclaredField("publisherId");
        publisherIdField.setAccessible(true);
        publisherIdField.set(publisher, 1L);

        book = Book.of(
                aladinBookRequestDTO.getTitle(),
                aladinBookRequestDTO.getDescription(),
                aladinBookRequestDTO.getPubDate(),
                aladinBookRequestDTO.getIsbn13(),
                aladinBookRequestDTO.getPriceStandard(),
                aladinBookRequestDTO.getPriceSales(),
                publisher,
                bookStatus
        );

        Field bookIdField = book.getClass().getDeclaredField("bookId");
        bookIdField.setAccessible(true);
        bookIdField.set(book, 1L);
    }

    @Test
    void searchAladinBooks() throws Exception {
        given(aladinApiService.searchBooks(any())).willReturn(List.of(aladinBookDTO));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/aladinApi")
                        .param("query", "Sample")
                        .header("Authorization", "Bearer dummy-token")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Book Title"))
                .andExpect(jsonPath("$[0].authors[0]").value("Author 1"))
                .andExpect(jsonPath("$[0].authors[1]").value("Author 2"))
                .andExpect(jsonPath("$[0].priceSales").value(20000))
                .andExpect(jsonPath("$[0].priceStandard").value(25000))
                .andExpect(jsonPath("$[0].publisher").value("Sample Publisher"))
                .andExpect(jsonPath("$[0].pubDate").value("2022-01-01"))
                .andExpect(jsonPath("$[0].isbn13").value(1234567890123L))
                .andExpect(jsonPath("$[0].description").value("This is a sample book description."))
                .andExpect(jsonPath("$[0].salesPoint").value(1000))
                .andExpect(jsonPath("$[0].cover").value("http://example.com/cover.jpg"))
                .andDo(document("search-aladin-books",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        queryParameters(
                                parameterWithName("query").description("검색어")
                        ),
                        responseFields(
                                fieldWithPath("[].title").description("책 제목"),
                                fieldWithPath("[].authors[]").description("저자 목록"),
                                fieldWithPath("[].priceSales").description("판매 가격"),
                                fieldWithPath("[].priceStandard").description("정가"),
                                fieldWithPath("[].publisher").description("출판사"),
                                fieldWithPath("[].pubDate").description("출판일"),
                                fieldWithPath("[].isbn13").description("ISBN13"),
                                fieldWithPath("[].description").description("책 설명"),
                                fieldWithPath("[].salesPoint").description("판매 포인트"),
                                fieldWithPath("[].cover").description("책 커버 이미지 URL")
                        )
                ));
    }

    @Test
    void createAladinBook() throws Exception {
        given(aladinApiService.createAladinBook(any(AladinBookRequestDTO.class))).willReturn(book);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/aladinApi")
                        .header("Authorization", "Bearer dummy-token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(aladinBookRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("Sample Book Title"))
                .andExpect(jsonPath("$.bookDescription").value("This is a sample book description."))
                .andExpect(jsonPath("$.bookPrice").value(20000))
                .andExpect(jsonPath("$.bookOriginPrice").value(25000))
                .andExpect(jsonPath("$.publisher.publisherName").value("Sample Publisher"))
                .andExpect(jsonPath("$.bookPubDate").value("2022-01-01"))
                .andExpect(jsonPath("$.bookIsbn").value(1234567890123L))
                .andExpect(jsonPath("$.bookWrappable").value(false))
                .andExpect(jsonPath("$.bookView").value(0))
                .andExpect(jsonPath("$.bookAmount").value(0))
                .andExpect(jsonPath("$.bookSellCount").value(0))
                .andDo(document("create-aladin-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        requestFields(
                                fieldWithPath("title").description("도서 제목"),
                                fieldWithPath("authors[]").description("저자 목록"),
                                fieldWithPath("priceSales").description("판매 가격"),
                                fieldWithPath("priceStandard").description("정가"),
                                fieldWithPath("publisher").description("출판사"),
                                fieldWithPath("bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("pubDate").description("출판일"),
                                fieldWithPath("isbn13").description("ISBN13"),
                                fieldWithPath("description").description("도서 설명"),
                                fieldWithPath("bookWrappable").description("포장 가능 여부"),
                                fieldWithPath("bookAmount").description("도서 수량")
                        ),
                        responseFields(
                                fieldWithPath("bookId").description("도서 아이디"),
                                fieldWithPath("bookTitle").description("도서 제목"),
                                fieldWithPath("bookDescription").description("도서 설명"),
                                fieldWithPath("bookPrice").description("판매 가격"),
                                fieldWithPath("bookOriginPrice").description("정가"),
                                fieldWithPath("publisher.publisherId").description("출판사 아이디"),
                                fieldWithPath("publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("bookStatus.bookStatusId").description("도서 상태 아이디"),
                                fieldWithPath("bookStatus.bookStatus").description("도서 상태 이름"),
                                fieldWithPath("publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("bookPubDate").description("출판일"),
                                fieldWithPath("bookIsbn").description("ISBN13"),
                                fieldWithPath("bookWrappable").description("포장 가능 여부"),
                                fieldWithPath("bookView").description("도서 조회수"),
                                fieldWithPath("bookAmount").description("도서 수량"),
                                fieldWithPath("bookSellCount").description("도서 판매 수량")
                        )
                ));
    }
}