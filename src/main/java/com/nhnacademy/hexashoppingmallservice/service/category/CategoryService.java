package com.nhnacademy.hexashoppingmallservice.service.category;

import com.nhnacademy.hexashoppingmallservice.dto.category.CategoryDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import com.nhnacademy.hexashoppingmallservice.exception.category.CategoryNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategoriesWithSubCategories() {
        List<Category> categories = categoryRepository.findAllFirstLevelWithSubCategories();
        Map<Long, CategoryDTO> categoryMap = new LinkedHashMap<>();

        for (Category category : categories) {
            CategoryDTO parentDTO = categoryMap.computeIfAbsent(category.getCategoryId(), id ->
                    new CategoryDTO(id, category.getCategoryName(), new ArrayList<>())
            );

            // 2차 카테고리 찾기
            List<Category> subCategories = categoryRepository.findByParentCategory(category);
            for (Category sub : subCategories) {
                parentDTO.getSubCategories().add(new CategoryDTO(sub.getCategoryId(), sub.getCategoryName(), null));
            }
        }

        return new ArrayList<>(categoryMap.values());
    }

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category insertCategory(Long categoryId, Long subCategoryId) {
        // 1차 카테고리 조회
        Category parentCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found. ID: %d".formatted(categoryId)));
        Category subCategory = categoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found. ID: %d".formatted(subCategoryId)));

        // 2차 카테고리에 부모 설정
        subCategory.setParentCategory(parentCategory);

        return parentCategory;
    }
}