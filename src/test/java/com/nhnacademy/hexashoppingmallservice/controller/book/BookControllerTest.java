package com.nhnacademy.hexashoppingmallservice.controller.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.service.book.BookService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collections;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = BookController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    private Publisher publisher;
    private BookStatus bookStatus;
    private Book book;
    private Author author;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        publisher = Publisher.of("Test Publisher");
        bookStatus = BookStatus.of("Test Book Status");
        author = Author.of("Test Author");

        Field publisherIdField = publisher.getClass().getDeclaredField("publisherId");
        publisherIdField.setAccessible(true);
        publisherIdField.set(publisher, 1L);

        Field bookStatusIdField = bookStatus.getClass().getDeclaredField("bookStatusId");
        bookStatusIdField.setAccessible(true);
        bookStatusIdField.set(bookStatus, 1L);

        Field authorIdField = author.getClass().getDeclaredField("authorId");
        authorIdField.setAccessible(true);
        authorIdField.set(author, 1L);

        book = Book.of(
                "Test Book",
                "Test Description",
                LocalDate.now(),
                1234567890123L,
                20000,
                18000,
                publisher,
                bookStatus
        );

        Field bookIdField = book.getClass().getDeclaredField("bookId");
        bookIdField.setAccessible(true);
        bookIdField.set(book, 1L);
    }


    @Test
    void getBooks() throws Exception {

        List<Book> books = Collections.singletonList(book);

        given(bookService.getBooksByBookTitle(anyString(), any(Pageable.class))).willReturn(books);
        given(bookService.getBooksByPublisherName(anyString(), any(Pageable.class))).willReturn(books);
        given(bookService.getBooksByCategory(anyList(), any(Pageable.class))).willReturn(books);
        given(bookService.getBooksByAuthorName(anyString(), any(Pageable.class))).willReturn(books);
        given(bookService.getBooksByBookView(any(Pageable.class))).willReturn(books);
        given(bookService.getBooksByBookSellCount(any(Pageable.class))).willReturn(books);
        given(bookService.getBooksByLikeCount(any(Pageable.class))).willReturn(books);
        given(bookService.getBooksByBookPubDate(any(Pageable.class))).willReturn(books);
        given(bookService.getBooks(any(Pageable.class))).willReturn(books);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/books")
                        .param("page", "0")
                        .param("search", "Test Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(books.size()))
                .andExpect(jsonPath("$[0].bookTitle").value("Test Book"))
                .andDo(document("get-books",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("search").description("도서 제목으로 검색"),
                                parameterWithName("categoryIds").description("카테고리(아이디)로 검색").optional(),
                                parameterWithName("publisherName").description("출판사명으로 검색").optional(),
                                parameterWithName("authorName").description("작가명으로 검색").optional(),
                                parameterWithName("sortByView").description("조회수에 의한 정렬").optional(),
                                parameterWithName("sortBySellCount").description("판매수에 의한 정렬").optional(),
                                parameterWithName("sortByLikeCount").description("좋아요수에 의한 정렬").optional(),
                                parameterWithName("latest").description("출간일 최신순으로 정렬").optional()
                        ),
                        responseFields(
                                fieldWithPath("[].bookId").description("도서 ID"),
                                fieldWithPath("[].bookTitle").description("도서 제목"),
                                fieldWithPath("[].bookDescription").description("도서 설명"),
                                fieldWithPath("[].bookPubDate").description("도서 출간일"),
                                fieldWithPath("[].bookIsbn").description("도서 ISBN"),
                                fieldWithPath("[].bookView").description("도서 페이지 조회수"),
                                fieldWithPath("[].bookAmount").description("도서 재고"),
                                fieldWithPath("[].bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("[].bookSellCount").description("도서 판매량"),
                                fieldWithPath("[].bookOriginPrice").description("도서 정가"),
                                fieldWithPath("[].bookPrice").description("도서 판매가"),
                                fieldWithPath("[].publisher.publisherId").description("출판사 ID"),
                                fieldWithPath("[].publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("[].bookStatus.bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("[].bookStatus.bookStatus").description("도서 상태")
                        )
                ));
    }

    @Test
    void createBook() throws Exception {
        BookRequestDTO requestDTO = new BookRequestDTO(
                "New Book",
                "Description of the new book",
                LocalDate.now(),
                1234567890123L,
                20000,
                18000,
                true,
                String.valueOf(publisher.getPublisherId()),
                String.valueOf(bookStatus.getBookStatusId())
        );

        Book createBook = Book.of(
                requestDTO.getBookTitle(),
                requestDTO.getBookDescription(),
                requestDTO.getBookPubDate(),
                requestDTO.getBookIsbn(),
                requestDTO.getBookOriginPrice(),
                requestDTO.getBookPrice(),
                publisher,
                bookStatus
        );

        Field bookIdField = createBook.getClass().getDeclaredField("bookId");
        bookIdField.setAccessible(true);
        bookIdField.set(createBook, 1L);

        given(bookService.createBook(any(BookRequestDTO.class))).willReturn(createBook);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/books")
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("New Book"))
                .andDo(document("create-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        requestFields(
                                fieldWithPath("bookTitle").description("도서 제목"),
                                fieldWithPath("bookDescription").description("도서 설명"),
                                fieldWithPath("bookPubDate").description("도서 출간일"),
                                fieldWithPath("bookIsbn").description("도서 ISBN"),
                                fieldWithPath("bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("bookOriginPrice").description("도서 정가"),
                                fieldWithPath("bookPrice").description("도서 판매가"),
                                fieldWithPath("publisherId").description("출판사 아이디"),
                                fieldWithPath("bookStatusId").description("도서 상태 아이디")

                        ),
                        responseFields(
                                fieldWithPath("bookId").description("도서 ID"),
                                fieldWithPath("bookTitle").description("도서 제목"),
                                fieldWithPath("bookDescription").description("도서 설명"),
                                fieldWithPath("bookPubDate").description("도서 출간일"),
                                fieldWithPath("bookIsbn").description("도서 ISBN"),
                                fieldWithPath("bookView").description("도서 페이지 조회수"),
                                fieldWithPath("bookAmount").description("도서 재고"),
                                fieldWithPath("bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("bookSellCount").description("도서 판매량"),
                                fieldWithPath("bookOriginPrice").description("도서 정가"),
                                fieldWithPath("bookPrice").description("도서 판매가"),
                                fieldWithPath("publisher.publisherId").description("출판사 ID"),
                                fieldWithPath("publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("bookStatus.bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("bookStatus.bookStatus").description("도서 상태")
                        )
                ));
    }

    @Test
    void getBook() throws Exception {

        given(bookService.getBook(anyLong())).willReturn(book);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/books/{bookId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Test Book"))
                .andDo(document("get-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        ),
                        responseFields(
                                fieldWithPath("bookId").description("도서 ID"),
                                fieldWithPath("bookTitle").description("도서 제목"),
                                fieldWithPath("bookDescription").description("도서 설명"),
                                fieldWithPath("bookPubDate").description("도서 출간일"),
                                fieldWithPath("bookIsbn").description("도서 ISBN"),
                                fieldWithPath("bookView").description("도서 페이지 조회수"),
                                fieldWithPath("bookAmount").description("도서 재고"),
                                fieldWithPath("bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("bookSellCount").description("도서 판매량"),
                                fieldWithPath("bookOriginPrice").description("도서 정가"),
                                fieldWithPath("bookPrice").description("도서 판매가"),
                                fieldWithPath("publisher.publisherId").description("출판사 ID"),
                                fieldWithPath("publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("bookStatus.bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("bookStatus.bookStatus").description("도서 상태")
                        )
                ));
    }


    @Test
    void updateBook() throws Exception {
        BookUpdateRequestDTO updateRequestDTO = new BookUpdateRequestDTO(
                "Updated Book Title",
                "Updated description",
                18000,
                true,
                String.valueOf(bookStatus.getBookStatusId())
        );

        book.setBookTitle(updateRequestDTO.getBookTitle());
        book.setBookDescription(updateRequestDTO.getBookDescription());
        book.setBookPrice(updateRequestDTO.getBookPrice());
        book.setBookWrappable(updateRequestDTO.getBookWrappable());
        book.setBookStatus(bookStatus);

        given(bookService.updateBook(anyLong(), any(BookUpdateRequestDTO.class))).willReturn(book);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/books/{bookId}", 1L)
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Updated Book Title"))
                .andExpect(jsonPath("$.bookDescription").value("Updated description"))
                .andDo(document("update-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        ),
                        requestFields(
                                fieldWithPath("bookTitle").description("도서 제목"),
                                fieldWithPath("bookDescription").description("도서 설명"),
                                fieldWithPath("bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("bookPrice").description("도서 판매가"),
                                fieldWithPath("statusId").description("도서 상태 ID")
                        ),
                        responseFields(
                                fieldWithPath("bookId").description("도서 ID"),
                                fieldWithPath("bookTitle").description("도서 제목"),
                                fieldWithPath("bookDescription").description("도서 설명"),
                                fieldWithPath("bookPubDate").description("도서 출간일"),
                                fieldWithPath("bookIsbn").description("도서 ISBN"),
                                fieldWithPath("bookView").description("도서 페이지 조회수"),
                                fieldWithPath("bookAmount").description("도서 재고"),
                                fieldWithPath("bookWrappable").description("도서 포장 가능 여부"),
                                fieldWithPath("bookSellCount").description("도서 판매량"),
                                fieldWithPath("bookOriginPrice").description("도서 정가"),
                                fieldWithPath("bookPrice").description("도서 판매가"),
                                fieldWithPath("publisher.publisherId").description("출판사 ID"),
                                fieldWithPath("publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("bookStatus.bookStatusId").description("도서 상태 ID"),
                                fieldWithPath("bookStatus.bookStatus").description("도서 상태")
                        )
                ));
    }

    @Test
    void deleteBook() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/books/{bookId}", 1L)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isNoContent())
                .andDo(document("delete-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        )
                ));
    }

    @Test
    void getAuthors() throws Exception {
        List<Author> authors = Collections.singletonList(author);
        given(bookService.getAuthors(anyLong())).willReturn(authors);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/books/{bookId}/authors", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorName").value("Test Author"))
                .andDo(document("get-book-authors",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].authorId").description("저자 ID"),
                                fieldWithPath("[].authorName").description("저자 이름")
                        )
                ));
    }

    @Test
    void incrementBookAmountIncrease() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/books/{bookId}/amount-increase", 1L)
                        .param("quantity", "1")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isNoContent())
                .andDo(document("increment-book-amount-increase",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("관리자 인증 토큰 (Bearer 형식)")
                        ),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        ),
                        queryParameters(
                                parameterWithName("quantity").description("도서 재고 증가 수")
                        )
                ));
    }

    @Test
    void incrementBookView() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/books/{bookId}/view", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("increment-book-view",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        )
                ));
    }


    @Test
    void incrementBookSellCount() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/books/{bookId}/sell-count", 1L)
                        .queryParam("quantity", "1"))
                .andExpect(status().isNoContent())
                .andDo(document("increment-book-sell-count",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("도서 ID")
                        ),
                        queryParameters(
                                parameterWithName("quantity").description("도서 판매량 증가 수")
                        )
                ));
    }

}
