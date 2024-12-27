package com.nhnacademy.hexashoppingmallservice.service.category;

import com.nhnacademy.hexashoppingmallservice.dto.category.CategoryDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.*;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.category.CategoryNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.category.BookCategoryRepository;
import com.nhnacademy.hexashoppingmallservice.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;
    private Category subCategory1;
    private Category subCategory2;
    private CategoryDTO categoryDTO;
    private Book book;


    @BeforeEach
    void setUp() {
        parentCategory = Category.of("Parent Category", null);
        subCategory1 = Category.of("Sub Category 1", parentCategory);
        subCategory2 = Category.of("Sub Category 2", parentCategory);

        Publisher publisher = Publisher.of("PublisherName");
        BookStatus bookStatus = BookStatus.of("Available");

        // 책 설정
        book = Book.of(
                "Sample Book",
                "Description of the sample book.",
                LocalDate.now(),
                1234567890123L,
                10000,
                12000,
                publisher,
                bookStatus
        );
    }

    // getAllCategoriesWithSubCategories 메서드 테스트
    @Test
    void testGetAllCategoriesWithSubCategories() {
        // Mocking
        when(categoryRepository.findAllFirstLevelWithSubCategories()).thenReturn(Arrays.asList(parentCategory));
        when(categoryRepository.findByParentCategory(parentCategory)).thenReturn(Arrays.asList(subCategory1, subCategory2));

        List<CategoryDTO> result = categoryService.getAllCategoriesWithSubCategories();

        assertNotNull(result);
        assertEquals(1, result.size());

        //parentCategory 확인
        CategoryDTO parentDTO = result.get(0);
        assertEquals(parentCategory.getCategoryId(), parentDTO.getCategoryId());
        assertEquals(parentCategory.getCategoryName(), parentDTO.getCategoryName());
        assertNotNull(parentDTO.getSubCategories());
        assertEquals(2, parentDTO.getSubCategories().size());

        //subCategory 확인
        CategoryDTO subDTO1 = parentDTO.getSubCategories().get(0);
        assertEquals(subCategory1.getCategoryId(), subDTO1.getCategoryId());
        assertEquals(subCategory1.getCategoryName(), subDTO1.getCategoryName());
        assertNull(subDTO1.getSubCategories());

        //subCategory 확인
        CategoryDTO subDTO2 = parentDTO.getSubCategories().get(1);
        assertEquals(subCategory2.getCategoryId(), subDTO2.getCategoryId());
        assertEquals(subCategory2.getCategoryName(), subDTO2.getCategoryName());
        assertNull(subDTO2.getSubCategories());

        verify(categoryRepository, times(1)).findAllFirstLevelWithSubCategories();
        verify(categoryRepository, times(1)).findByParentCategory(parentCategory);
    }

    // createCategory 메서드 테스트
//    @Test
//    void testCreateCategory() {
//
//        Category newCategory = Category.of("New Category", null);
//
//        when(categoryRepository.save(newCategory)).thenReturn(newCategory);
//
//        Category result = categoryService.createCategory(newCategory);
//
//        assertNotNull(result);
//        assertEquals(newCategory.getCategoryId(), result.getCategoryId());
//        assertEquals("New Category", result.getCategoryName());
//
//        verify(categoryRepository, times(1)).save(newCategory);
//    }

    // insertCategory 메서드 성공 시 테스트
    @Test
    void testInsertCategory_Success() {

        Long parentId = 1L;
        Long subId = 2L;

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findById(subId)).thenReturn(Optional.of(subCategory1));

        Category result = categoryService.insertCategory(parentId, subId);

        assertNotNull(result);
        assertEquals(parentCategory, result);
        assertEquals(parentCategory, subCategory1.getParentCategory());

        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryRepository, times(1)).findById(subId);
    }

    // insertCategory 메서드 부모 카테고리 없음 테스트
    @Test
    void testInsertCategory_ParentNotFound() {

        Long parentId = 1L;
        Long subId = 2L;

        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.insertCategory(parentId, subId)
        );

        assertEquals("Category Not Found. ID: 1", exception.getMessage());

        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryRepository, never()).findById(subId);
    }

    // insertCategory 메서드 서브 카테고리 없음 테스트
    @Test
    void testInsertCategory_SubNotFound() {

        Long parentId = 1L;
        Long subId = 2L;

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findById(subId)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.insertCategory(parentId, subId)
        );

        assertEquals("Category Not Found. ID: 2", exception.getMessage());

        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryRepository, times(1)).findById(subId);
    }

    // insertBook 메서드 성공 시 테스트
    @Test
    void testInsertBook_Success() {

        Long categoryId = 1L;
        Long bookId = 10L;
        Category category = parentCategory;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.insertBook(categoryId, bookId);

        ArgumentCaptor<BookCategory> captor = ArgumentCaptor.forClass(BookCategory.class);
        verify(bookCategoryRepository, times(1)).save(captor.capture());

        BookCategory savedBookCategory = captor.getValue();
        assertEquals(category, savedBookCategory.getCategory());
        assertEquals(book, savedBookCategory.getBook());
    }

    // insertBook 메서드 카테고리 없음 테스트
    @Test
    void testInsertBook_CategoryNotFound() {

        Long categoryId = 1L;
        Long bookId = 10L;

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.insertBook(categoryId, bookId)
        );

        assertEquals("Category Not Found. ID: 1", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookRepository, never()).existsById(anyLong());
        verify(bookCategoryRepository, never()).save(any());
    }

    // insertBook 메서드 책 없음 테스트
    @Test
    void testInsertBook_BookNotFound() {

        Long categoryId = 1L;
        Long bookId = 10L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookRepository.existsById(bookId)).thenReturn(false);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                categoryService.insertBook(categoryId, bookId)
        );

        assertEquals("Book Not Found. ID: 10", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookCategoryRepository, never()).save(any());
    }
}
