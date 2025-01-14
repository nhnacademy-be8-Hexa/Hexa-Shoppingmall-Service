package com.nhnacademy.hexashoppingmallservice.repository.book.querydsl;

import com.nhnacademy.hexashoppingmallservice.entity.book.Category;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> findAllFirstLevelWithSubCategories();
}
