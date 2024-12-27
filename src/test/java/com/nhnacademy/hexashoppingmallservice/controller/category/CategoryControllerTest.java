package com.nhnacademy.hexashoppingmallservice.controller.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.category.CategoryDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.service.category.CategoryService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtils jwtUtils;

    private String validToken;

    private Category parentCategory;
    private Category subCategory;
    private Book book;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        validToken = "Bearer valid.jwt.token";

        parentCategory = Category.of("Electronics", null);
        Field parentCategoryField = parentCategory.getClass().getDeclaredField("categoryId");
        parentCategoryField.setAccessible(true);
        parentCategoryField.set(parentCategory, 1L);

        subCategory = Category.of("Computer", null);
        Field subCategoryField = subCategory.getClass().getDeclaredField("categoryId");
        subCategoryField.setAccessible(true);
        subCategoryField.set(subCategory, 2L);


        Publisher publisher = Publisher.of("Penguin Books");
        Field publisherIdField = Publisher.class.getDeclaredField("publisherId");
        publisherIdField.setAccessible(true);
        publisherIdField.set(publisher, 1L);

        BookStatus bookStatus = BookStatus.of("Available");
        Field bookStatusIdField = BookStatus.class.getDeclaredField("bookStatusId");
        bookStatusIdField.setAccessible(true);
        bookStatusIdField.set(bookStatus, 1L);

        Book book = Book.of(
                "The Great Gatsby",
                "A classic novel by F. Scott Fitzgerald.",
                LocalDate.parse("1925-04-10"),
                1234567890123L,
                20000,
                15000,
                publisher,
                bookStatus
        );

        Field bookIdField = Book.class.getDeclaredField("bookId");
        bookIdField.setAccessible(true);
        bookIdField.set(book, 1L);

    }

    @Test
    void createCategory() throws Exception {
        Mockito.when(categoryService.createCategory(any(Category.class))).thenReturn(parentCategory);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/categories")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parentCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("Electronics"))
                .andDo(document("create-category",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("categoryId").description("카테고리 ID").optional(),
                                fieldWithPath("categoryName").description("카테고리 이름"),
                                fieldWithPath("parentCategory").description("상위 카테고리").optional()
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        responseFields(
                                fieldWithPath("categoryId").description("생성된 카테고리 ID"),
                                fieldWithPath("categoryName").description("생성된 카테고리 이름"),
                                fieldWithPath("parentCategory").description("상위 카테고리 정보").optional()
                        )
                ));
    }


    @Test
    void insertCategory() throws Exception {
        // 초기 상태: subCategory는 부모 카테고리가 없음
        subCategory = Category.of("Novel", null);
        Field subCategoryField = subCategory.getClass().getDeclaredField("categoryId");
        subCategoryField.setAccessible(true);
        subCategoryField.set(subCategory, 2L);

        // parentCategory 생성
        parentCategory = Category.of("Fiction", null);
        Field parentCategoryField = parentCategory.getClass().getDeclaredField("categoryId");
        parentCategoryField.setAccessible(true);
        parentCategoryField.set(parentCategory, 1L);

        // 서비스 레이어에서 부모 카테고리에 서브 카테고리를 연결하는 로직이 처리됨
        Mockito.when(categoryService.insertCategory(1L, 2L)).thenReturn(parentCategory);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/categories/{categoryId}/subcategories/{subCategoryId}", 1L, 2L)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Fiction"))
                .andExpect(jsonPath("$.parentCategory").doesNotExist())
                .andDo(document("insert-category",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("categoryId").description("부모 카테고리 ID"),
                                parameterWithName("subCategoryId").description("삽입할 서브 카테고리 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증 토큰 (Bearer 형식)")
                        ),
                        responseFields(
                                fieldWithPath("categoryId").description("부모 카테고리 ID"),
                                fieldWithPath("categoryName").description("부모 카테고리 이름"),
                                fieldWithPath("parentCategory").description("부모 카테고리의 상위 정보").optional()
                        )
                ));
    }







    @Test
    void getAllCategoriesWithSubCategories() throws Exception {
        // CategoryDTO 생성
        CategoryDTO subCategoryDTO = new CategoryDTO(2L, "Computer", List.of());
        CategoryDTO parentCategoryDTO = new CategoryDTO(1L, "Electronics", List.of(subCategoryDTO));

        // Mockito 설정
        Mockito.when(categoryService.getAllCategoriesWithSubCategories()).thenReturn(List.of(parentCategoryDTO));

        // MockMvc 요청
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-all-categories",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].categoryId").description("카테고리 ID"),
                                fieldWithPath("[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].subCategories").description("하위 카테고리 리스트").optional(),
                                fieldWithPath("[].subCategories[].categoryId").description("하위 카테고리 ID").optional(),
                                fieldWithPath("[].subCategories[].categoryName").description("하위 카테고리 이름").optional(),
                                fieldWithPath("[].subCategories[].subCategories").description("하위 카테고리의 하위 리스트").optional()
                        )
                ));
    }


    @Test
    void addBookToCategory() throws Exception {
        // Mock 호출 없이 단순 API 테스트
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/categories/{categoryId}/books/{bookId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("add-book-to-category",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("categoryId").description("카테고리 ID"),
                                parameterWithName("bookId").description("도서 ID")
                        )
                ));
    }
}
