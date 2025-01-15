package com.nhnacademy.hexashoppingmallservice.repository.category;

import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 1차 카테고리와 그에 속한 2차 카테고리를 조인하여 조회
//    @Query("SELECT c FROM Category c LEFT JOIN FETCH Category sc ON sc.parentCategory = c WHERE c.parentCategory IS NULL")
//    List<Category> findAllFirstLevelWithSubCategories();

    // 부모 카테고리를 기준으로 서브카테고리를 조회
    List<Category> findByParentCategory(Category parentCategory);

    Category findByCategoryName(String categoryName);
}
