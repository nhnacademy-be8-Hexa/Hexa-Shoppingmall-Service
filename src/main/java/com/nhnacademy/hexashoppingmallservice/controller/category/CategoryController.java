package com.nhnacademy.hexashoppingmallservice.controller.category;

import com.nhnacademy.hexashoppingmallservice.dto.category.CategoryDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import com.nhnacademy.hexashoppingmallservice.service.category.CategoryService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;

    /**
     * 1차 카테고리 생성 엔드포인트
     *
     * @param category 생성할 1차 카테고리 정보
     * @return 생성된 카테고리 정보
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category, HttpServletRequest request) {
        // 1차 카테고리 생성
        jwtUtils.ensureAdmin(request);
        Category createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * 특정 1차 카테고리에 2차 카테고리 삽입 엔드포인트
     *
     * @param categoryId 부모 1차 카테고리의 ID
     * @param subCategoryId 2차 카테고리의 ID
     * @return 생성된 2차 카테고리 정보
     */
    @PostMapping("/{categoryId}/subcategories/{subCategoryId}")
    public ResponseEntity<Category> insertCategory(
            @PathVariable Long categoryId,
            @PathVariable Long subCategoryId,
            HttpServletRequest request) {
        // 2차 카테고리 삽입
        jwtUtils.ensureAdmin(request);
        Category insertedCategory = categoryService.insertCategory(categoryId, subCategoryId);
        return new ResponseEntity<>(insertedCategory, HttpStatus.CREATED);
    }

    /**
     * 모든 1차 카테고리와 그에 속한 2차 카테고리 조회 엔드포인트
     *
     * @return 1차 카테고리와 그에 속한 2차 카테고리 목록
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesWithSubCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategoriesWithSubCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * 카테고리 삭제 엔드포인트
     *
     * @param categoryId 삭제할 카테고리의 ID
     * @return 삭제 성공 메시지
     */
//    @DeleteMapping("/{categoryId}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
//        categoryService.deleteCategory(categoryId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    @PostMapping("/{categoryId}/books/{bookId}")
    public ResponseEntity<Void> getAllBooksByCategoryId(@PathVariable Long categoryId, @PathVariable Long bookId) {
        categoryService.insertBook(categoryId, bookId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
