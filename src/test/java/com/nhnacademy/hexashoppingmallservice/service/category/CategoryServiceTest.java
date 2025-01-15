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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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

//     createCategory 메서드 테스트
    @Test
    void testCreateCategory2() {

        Category newCategory = Category.of("New Category", null);

        when(categoryRepository.save(newCategory)).thenReturn(newCategory);

        Category result = categoryService.createCategory(newCategory);

        assertNotNull(result);
        assertEquals(newCategory.getCategoryId(), result.getCategoryId());
        assertEquals("New Category", result.getCategoryName());

        verify(categoryRepository, times(1)).save(newCategory);
    }

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

    // createCategory 메서드 테스트
    @Test
    void testCreateCategory() {
        Category newCategory = Category.of("New Category", null);

        when(categoryRepository.save(newCategory)).thenReturn(newCategory);

        Category result = categoryService.createCategory(newCategory);

        assertNotNull(result);
        assertEquals(newCategory.getCategoryId(), result.getCategoryId());
        assertEquals("New Category", result.getCategoryName());

        verify(categoryRepository, times(1)).save(newCategory);
    }

    // insertBooks 메서드 성공 시 테스트
    @Test
    void testInsertBooks_Success() {
        Long categoryId = 1L;
        List<Long> bookIds = Arrays.asList(10L, 20L);
        Category category = parentCategory;
        Book book1 = Book.of("Book 1", "Description 1", LocalDate.now(), 1234567890123L, 10000, 12000, Publisher.of("Publisher1"), BookStatus.of("Available"));
        Book book2 = Book.of("Book 2", "Description 2", LocalDate.now(), 1234567890124L, 15000, 18000, Publisher.of("Publisher2"), BookStatus.of("Available"));

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookRepository.existsById(10L)).thenReturn(true);
        when(bookRepository.existsById(20L)).thenReturn(true);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(20L)).thenReturn(Optional.of(book2));

        categoryService.insertBooks(categoryId, bookIds);

        ArgumentCaptor<BookCategory> captor = ArgumentCaptor.forClass(BookCategory.class);
        verify(bookCategoryRepository, times(2)).save(captor.capture());

        List<BookCategory> savedBookCategories = captor.getAllValues();
        assertEquals(category, savedBookCategories.get(0).getCategory());
        assertEquals(book1, savedBookCategories.get(0).getBook());
        assertEquals(category, savedBookCategories.get(1).getCategory());
        assertEquals(book2, savedBookCategories.get(1).getBook());
    }

    // insertBooks 메서드 카테고리 없음 테스트
    @Test
    void testInsertBooks_CategoryNotFound() {
        Long categoryId = 1L;
        List<Long> bookIds = Arrays.asList(10L, 20L);

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.insertBooks(categoryId, bookIds)
        );

        assertEquals("Category Not Found. ID: 1", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookRepository, never()).existsById(anyLong());
        verify(bookCategoryRepository, never()).save(any());
    }

    // insertBooks 메서드 책 없음 테스트
    @Test
    void testInsertBooks_BookNotFound() {
        Long categoryId = 1L;
        List<Long> bookIds = List.of(20L);
        Category category = Category.of("Category", null);


        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookRepository.existsById(20L)).thenReturn(false); // 2번째 책이 존재하지 않음
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
                categoryService.insertBooks(categoryId, bookIds)
        );

        assertEquals("Book Not Found. ID: 20", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookRepository, times(1)).existsById(anyLong());
        verify(bookCategoryRepository, never()).save(any());
    }

    // getAllPagedCategories 메서드 테스트
//    @Test
//    void testGetAllPagedCategories() {
//        Pageable pageable = Pageable.unpaged();
//        Category category1 = Category.of("Category 1", null);
//        Category category2 = Category.of("Category 2", null);
//        List<Category> categories = Arrays.asList(category1, category2);
//
//        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(categories));
//
//        List<Category> result = categoryService.getAllPagedCategories(pageable);
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("Category 1", result.get(0).getCategoryName());
//        assertEquals("Category 2", result.get(1).getCategoryName());
//
//        verify(categoryRepository, times(1)).findAll(pageable);
//    }

    // getAllCategories 메서드 테스트
    @Test
    void testGetAllCategories() {
        Category category1 = Category.of("Category 1", null);
        Category category2 = Category.of("Category 2", null);
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getCategoryName());
        assertEquals("Category 2", result.get(1).getCategoryName());

        verify(categoryRepository, times(1)).findAll();
    }

    // getTotal 메서드 테스트
    @Test
    void testGetTotal() {
        when(categoryRepository.count()).thenReturn(5L);

        Long result = categoryService.getTotal();

        assertNotNull(result);
        assertEquals(5L, result);

        verify(categoryRepository, times(1)).count();
    }

    // deleteCategory 메서드 성공 시 테스트
    @Test
    void testDeleteCategory_Success() {
        Long categoryId = 1L;
        Category category = parentCategory;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByParentCategory(category)).thenReturn(Arrays.asList(subCategory1, subCategory2));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).delete(category);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findByParentCategory(category);
    }

    // deleteCategory 메서드 카테고리 없음 테스트
    @Test
    void testDeleteCategory_CategoryNotFound() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.deleteCategory(categoryId)
        );

        assertEquals("Category Not Found. ID: 1", exception.getMessage());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).findByParentCategory(any());
        verify(categoryRepository, never()).delete(any());
    }

    // getAllCategoriesByBookId 메서드 테스트
    @Test
    void testGetAllCategoriesByBookId() {
        Long bookId = 10L;

        // BookCategory 객체를 생성하여 카테고리와 연결
        BookCategory bookCategory1 = BookCategory.of(parentCategory, book);
        BookCategory bookCategory2 = BookCategory.of(subCategory1, book);

        List<BookCategory> bookCategories = Arrays.asList(bookCategory1, bookCategory2);

        // Mocking: bookRepository에서 bookId로 책이 존재한다고 반환
        when(bookRepository.existsById(bookId)).thenReturn(true);

        // Mocking: bookCategoryRepository에서 bookId에 해당하는 BookCategory 목록을 반환
        when(bookCategoryRepository.findByBook_BookId(bookId)).thenReturn(bookCategories);

        // 테스트 실행
        List<Category> result = categoryService.getAllCategoriesByBookId(bookId);

        // 결과 검증
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Parent Category", result.get(0).getCategoryName());
        assertEquals("Sub Category 1", result.get(1).getCategoryName());

        // Verify mocking calls
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookCategoryRepository, times(1)).findByBook_BookId(bookId);
    }




    // deleteByCategoryIdAndBookId 메서드 테스트
    @Test
    void testDeleteByCategoryIdAndBookId_Success() {
        Long categoryId = 1L;
        Long bookId = 10L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookRepository.existsById(bookId)).thenReturn(true);

        categoryService.deleteByCategoryIdAndBookId(categoryId, bookId);

        verify(bookCategoryRepository, times(1)).deleteByCategory_CategoryIdAndBook_BookId(categoryId, bookId);
    }

    // deleteByCategoryIdAndBookId 메서드 카테고리 없음 테스트
    @Test
    void testDeleteByCategoryIdAndBookId_CategoryNotFound() {
        Long categoryId = 1L;
        Long bookId = 10L;

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.deleteByCategoryIdAndBookId(categoryId, bookId)
        );

        assertEquals("Category Not Found. ID: 1", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookRepository, never()).existsById(bookId);
        verify(bookCategoryRepository, never()).deleteByCategory_CategoryIdAndBook_BookId(any(), any());
    }

    // deleteByCategoryIdAndBookIds 메서드 테스트
    @Test
    void testDeleteByCategoryIdAndBookIds_Success() {
        Long categoryId = 1L;
        List<Long> bookIds = Arrays.asList(10L, 20L);

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        categoryService.deleteByCategoryIdAndBookIds(categoryId, bookIds);

        verify(bookCategoryRepository, times(1)).deleteByCategory_CategoryIdAndBook_BookIdIn(categoryId, bookIds);
    }

    // deleteByCategoryIdAndBookIds 메서드 카테고리 없음 테스트
    @Test
    void testDeleteByCategoryIdAndBookIds_CategoryNotFound() {
        Long categoryId = 1L;
        List<Long> bookIds = Arrays.asList(10L, 20L);

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.deleteByCategoryIdAndBookIds(categoryId, bookIds)
        );

        assertEquals("Category Not Found. ID: 1", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(bookCategoryRepository, never()).deleteByCategory_CategoryIdAndBook_BookIdIn(any(), any());
    }

}
