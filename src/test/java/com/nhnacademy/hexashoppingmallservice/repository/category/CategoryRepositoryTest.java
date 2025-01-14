package com.nhnacademy.hexashoppingmallservice.repository.category;

import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category parentCategory;
    private Category subCategory1;
    private Category subCategory2;
    private Category subCategory3;

    @BeforeEach
    void setUp() {
        // 부모 카테고리 저장
        parentCategory = Category.of("Electronics", null);
        categoryRepository.save(parentCategory);

        // 서브 카테고리 저장
        subCategory1 = Category.of("Computers", parentCategory);
        subCategory2 = Category.of("Smartphones", parentCategory);
        subCategory3 = Category.of("Laptops", subCategory1);
        categoryRepository.saveAll(Arrays.asList(subCategory1, subCategory2, subCategory3));
    }

//    @Test
//    @DisplayName("findAllFirstLevelWithSubCategories()가 부모 카테고리와 그 서브 카테고리를 정확히 조회하는지 테스트")
//    void testFindAllFirstLevelWithSubCategories() {
//
//        List<Category> firstLevelCategories = categoryRepository.findAllFirstLevelWithSubCategories();
//
//        assertThat(firstLevelCategories).hasSize(1);
//        Category fetchedParent = firstLevelCategories.get(0);
//        assertThat(fetchedParent.getCategoryName()).isEqualTo("Electronics");
//
//        List<Category> fetchedSubCategories = categoryRepository.findByParentCategory(fetchedParent);
//        assertThat(fetchedSubCategories).hasSize(2);
//
//        assertThat(fetchedSubCategories)
//                .extracting(Category::getCategoryName)
//                .containsExactlyInAnyOrder("Computers", "Smartphones");
//    }

    @Test
    @DisplayName("findByParentCategory()가 주어진 부모 카테고리의 모든 서브 카테고리를 정확히 조회하는지 테스트")
    void testFindByParentCategory() {

        Category parent = parentCategory;

        List<Category> subCategories = categoryRepository.findByParentCategory(parent);

        assertThat(subCategories).hasSize(2);
        assertThat(subCategories)
                .extracting(Category::getCategoryName)
                .containsExactlyInAnyOrder("Computers", "Smartphones");
    }

    @Test
    @DisplayName("findByParentCategory()가 서브 카테고리를 가지지 않는 카테고리에 대해 빈 리스트를 반환하는지 테스트")
    void testFindByParentCategory_NoSubCategories() {

        Category leafCategory = subCategory3;

        List<Category> subCategories = categoryRepository.findByParentCategory(leafCategory);

        assertThat(subCategories).isEmpty();
    }

//    @Test
//    @DisplayName("findAllFirstLevelWithSubCategories()가 서브 카테고리가 없는 부모 카테고리를 정확히 조회하는지 테스트")
//    void testFindAllFirstLevelWithSubCategories_NoSubCategories() {
//
//        Category newParent = Category.of("Books", null);
//        categoryRepository.save(newParent);
//
//        List<Category> firstLevelCategories = categoryRepository.findAllFirstLevelWithSubCategories();
//
//        assertThat(firstLevelCategories).hasSize(2);
//        assertThat(firstLevelCategories)
//                .extracting(Category::getCategoryName)
//                .containsExactlyInAnyOrder("Electronics", "Books");
//
//        // "Books" 카테고리의 서브 카테고리가 없음을 검증
//        for (Category category : firstLevelCategories) {
//            List<Category> subCats = categoryRepository.findByParentCategory(category);
//            if (category.getCategoryName().equals("Electronics")) {
//                assertThat(subCats).hasSize(2);
//            } else if (category.getCategoryName().equals("Books")) {
//                assertThat(subCats).isEmpty();
//            }
//        }
//    }
}
