package com.nhnacademy.hexashoppingmallservice.controller.tag;

import com.nhnacademy.hexashoppingmallservice.entity.book.Author;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.book.Tag;
import com.nhnacademy.hexashoppingmallservice.service.tag.BookTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = BookTagController.class)
@AutoConfigureRestDocs
class BookTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookTagService bookTagService;

    private Publisher publisher;
    private BookStatus bookStatus;
    private Author author;
    private Book book;
    private Tag tag;

    @BeforeEach
    void setUp() throws Exception {
        // Mock Publisher, BookStatus, Author 생성
        publisher = Publisher.of("Test Publisher");
        bookStatus = BookStatus.of("Test Book Status");
        author = Author.of("Test Author");

        // Reflection으로 ID 설정
        setFieldValue(publisher, "publisherId", 1L);
        setFieldValue(bookStatus, "bookStatusId", 1L);
        setFieldValue(author, "authorId", 1L);

        // Book 객체 생성
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

        // Reflection으로 Book ID 설정
        setFieldValue(book, "bookId", 1L);

        // Tag 객체 생성
        tag = Tag.of("Test Tag");
        setFieldValue(tag, "tagId", 1L);
    }

    // Reflection으로 private 필드 값 설정
    private void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void addBookTag() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/admin/books/{bookId}/tags/{tagId}", 1L, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("add-book-tag",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("태그를 추가할 책 ID"),
                                parameterWithName("tagId").description("추가할 태그 ID")
                        )
                ));

        verify(bookTagService).create(eq(1L), eq(1L));
    }

    @Test
    void getTagsByBook() throws Exception {
        List<Tag> mockTags = List.of(tag);

        Mockito.when(bookTagService.getTagsByBookId(1L)).thenReturn(mockTags);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/books/{bookId}/tags", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-tags-by-book",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("태그를 조회할 책 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].tagId").description("태그 ID"),
                                fieldWithPath("[].tagName").description("태그 이름")
                        )
                ));

        verify(bookTagService).getTagsByBookId(eq(1L));
    }

    @Test
    void getBooksByTag() throws Exception {
        List<Book> mockBooks = List.of(book);

        Mockito.when(bookTagService.getBooksByTagId(eq(1L), any())).thenReturn(mockBooks);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tags/{tagId}/books", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-books-by-tag",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("tagId").description("책을 조회할 태그 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].bookId").description("책 ID"),
                                fieldWithPath("[].bookTitle").description("책 제목"),
                                fieldWithPath("[].bookDescription").description("책 설명"),
                                fieldWithPath("[].bookPubDate").description("출판일"),
                                fieldWithPath("[].bookIsbn").description("ISBN 번호"),
                                fieldWithPath("[].bookOriginPrice").description("원래 가격"),
                                fieldWithPath("[].bookPrice").description("판매 가격"),
                                fieldWithPath("[].bookWrappable").description("포장 가능 여부"),
                                fieldWithPath("[].bookView").description("조회 수"),
                                fieldWithPath("[].bookAmount").description("재고 수량"),
                                fieldWithPath("[].bookSellCount").description("판매 수량"),
                                fieldWithPath("[].publisher.publisherId").description("출판사 ID"),
                                fieldWithPath("[].publisher.publisherName").description("출판사 이름"),
                                fieldWithPath("[].bookStatus.bookStatusId").description("책 상태 ID"),
                                fieldWithPath("[].bookStatus.bookStatus").description("책 상태 이름")
                        )
                ));

        verify(bookTagService).getBooksByTagId(eq(1L), any());
    }


    @Test
    void deleteBookTag() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/books/{bookId}/tags/{tagId}", 1L, 1L))
                .andExpect(status().isOk())
                .andDo(document("delete-book-tag",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bookId").description("태그를 삭제할 책 ID"),
                                parameterWithName("tagId").description("삭제할 태그 ID")
                        )
                ));

        verify(bookTagService).delete(eq(1L), eq(1L));
    }
}
