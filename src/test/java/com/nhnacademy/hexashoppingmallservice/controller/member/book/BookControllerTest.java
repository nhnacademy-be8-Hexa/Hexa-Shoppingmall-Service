package com.nhnacademy.hexashoppingmallservice.controller.member.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.controller.book.BookController;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookRequestDTO;
import com.nhnacademy.hexashoppingmallservice.dto.book.BookUpdateRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookStatusRepository;
import com.nhnacademy.hexashoppingmallservice.repository.book.PublisherRepository;
import com.nhnacademy.hexashoppingmallservice.service.book.BookService;
import com.nhnacademy.hexashoppingmallservice.service.book.BookStatusService;
import com.nhnacademy.hexashoppingmallservice.service.book.PublisherService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    private PublisherService publisherService;

    @MockBean
    private BookStatusService bookStatusService;

    @MockBean
    private PublisherRepository publisherRepository;

    @MockBean
    private BookStatusRepository bookStatusRepository;

    @MockBean
    private BookRepository bookRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Publisher publisher;
    private BookStatus bookStatus;

    @BeforeEach
    void setUp() {
        publisher = Publisher.of("publisher1");
        bookStatus = BookStatus.of("available");

        given(publisherRepository.findById(anyLong())).willReturn(Optional.of(publisher));
        given(bookStatusRepository.findById(anyLong())).willReturn(Optional.of(bookStatus));
    }

    @Test
    void createBook() throws Exception {
        BookRequestDTO requestDTO = new BookRequestDTO(
                "Book Title",
                "This is a description of the book.",
                LocalDate.of(2020, 1, 1),
                1234567890123L,
                2000,
                1500,
                true,
                "Publisher1",
                "Available"
        );

        Book createdBook =
                Book.of("Book Title", "This is a description of the book.", LocalDate.of(2020, 1, 1), 1234567890123L,
                        2000, 1500, publisher, bookStatus);

        given(bookService.createBook(any(BookRequestDTO.class))).willReturn(createdBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("Book Title"))
                .andExpect(jsonPath("$.bookDescription").value("This is a description of the book."))
                .andExpect(jsonPath("$.bookPubDate").value("2020-01-01"))
                .andDo(document("create-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("bookTitle").description("책 제목"),
                                fieldWithPath("bookDescription").description("책 설명"),
                                fieldWithPath("bookPubDate").description("책 출판일"),
                                fieldWithPath("bookIsbn").description("책 ISBN"),
                                fieldWithPath("bookOriginPrice").description("책 원가"),
                                fieldWithPath("bookPrice").description("책 판매가"),
                                fieldWithPath("bookWrappable").description("책 포장 가능 여부"),
                                fieldWithPath("publisherId").description("출판사 ID"),
                                fieldWithPath("bookStatusId").description("책 상태 ID")
                        ),
                        responseFields(
                                fieldWithPath("bookId").description("책 ID"),
                                fieldWithPath("bookTitle").description("책 제목"),
                                fieldWithPath("bookDescription").description("책 설명"),
                                fieldWithPath("bookPubDate").description("책 출판일"),
                                fieldWithPath("bookIsbn").description("책 ISBN"),
                                fieldWithPath("bookPrice").description("책 판매가"),
                                fieldWithPath("publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("bookStatus.statusName").description("책 상태 이름")
                        )
                ));
    }

    @Test
    void getBook() throws Exception {
        Book book =
                Book.of("Book Title", "This is a description of the book.", LocalDate.of(2020, 1, 1), 1234567890123L,
                        2000, 1500, publisher, bookStatus);

        Long bookdId = book.getBookId();
        given(bookService.getBook(bookdId)).willReturn(book);

        mockMvc.perform(get("/api/books/{bookId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Book Title"))
                .andExpect(jsonPath("$.bookDescription").value("This is a description of the book."))
                .andDo(document("get-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("조회할 책 ID")
                        ),
                        responseFields(
                                fieldWithPath("bookId").description("책 ID"),
                                fieldWithPath("bookTitle").description("책 제목"),
                                fieldWithPath("bookDescription").description("책 설명"),
                                fieldWithPath("bookPubDate").description("책 출판일"),
                                fieldWithPath("bookIsbn").description("책 ISBN"),
                                fieldWithPath("bookPrice").description("책 판매가"),
                                fieldWithPath("publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("bookStatus.statusName").description("책 상태 이름")
                        )
                ));
    }

    @Test
    void getBooks() throws Exception {
        List<Book> mockBooks = List.of(
                Book.of("Book 1", "Description 1", LocalDate.of(2020, 1, 1), 1234567890123L, 1500,
                        1500, publisher, bookStatus),
                Book.of("Book 2", "Description 2", LocalDate.of(2021, 1, 1), 1234567890124L, 1600,
                        1600, publisher, bookStatus)
        );

        given(bookService.getBooks(any(Pageable.class))).willReturn(mockBooks);

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockBooks.size()))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].bookTitle").value("Book 1"))
                .andDo(document("get-books",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호")
                        ),
                        responseFields(
                                fieldWithPath("[].bookId").description("책 ID"),
                                fieldWithPath("[].bookTitle").description("책 제목"),
                                fieldWithPath("[].bookDescription").description("책 설명"),
                                fieldWithPath("[].bookPubDate").description("책 출판일"),
                                fieldWithPath("[].bookIsbn").description("책 ISBN"),
                                fieldWithPath("[].bookPrice").description("책 판매가")
                        )
                ));

        verify(bookService).getBooks(any(Pageable.class));
    }

    @Test
    void updateBook() throws Exception {
        BookRequestDTO requestDTO = new BookRequestDTO(
                "Updated Book Title",
                "Updated description.",
                LocalDate.of(2022, 1, 1),
                1234567890125L,
                2200,
                1700,
                true,
                "Publisher1",
                "Available"
        );

        Book updatedBook =
                Book.of("Updated Book Title", "Updated description.", LocalDate.of(2022, 1, 1), 1234567890125L, 2200,
                        1700, publisher, bookStatus);

        given(bookService.updateBook(anyLong(), any(BookUpdateRequestDTO.class))).willReturn(updatedBook);

        mockMvc.perform(patch("/api/books/{bookId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Updated Book Title"))
                .andExpect(jsonPath("$.bookDescription").value("Updated description."))
                .andExpect(jsonPath("$.bookPrice").value(1700))
                .andDo(document("update-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("bookTitle").description("책 제목"),
                                fieldWithPath("bookDescription").description("책 설명"),
                                fieldWithPath("bookPubDate").description("책 출판일"),
                                fieldWithPath("bookIsbn").description("책 ISBN"),
                                fieldWithPath("bookPrice").description("책 판매가")
                        ),
                        responseFields(
                                fieldWithPath("bookId").description("책 ID"),
                                fieldWithPath("bookTitle").description("책 제목"),
                                fieldWithPath("bookDescription").description("책 설명"),
                                fieldWithPath("bookPubDate").description("책 출판일"),
                                fieldWithPath("bookIsbn").description("책 ISBN"),
                                fieldWithPath("bookPrice").description("책 판매가")
                        )
                ));
    }

}
