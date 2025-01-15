package com.nhnacademy.hexashoppingmallservice.repository.querydsl.impl;

import com.nhnacademy.hexashoppingmallservice.entity.book.Category;
import com.nhnacademy.hexashoppingmallservice.entity.book.QCategory;
import com.nhnacademy.hexashoppingmallservice.repository.querydsl.CategoryRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryRepositoryCustomImpl extends QuerydslRepositorySupport implements CategoryRepositoryCustom {

    public CategoryRepositoryCustomImpl() {
        super(Category.class);
    }

    @Override
    public List<Category> findAllFirstLevelWithSubCategories() {
        QCategory category = QCategory.category;
        QCategory subCategory = new QCategory("subCategory");

        return from(category)
                .leftJoin(subCategory).on(subCategory.parentCategory.eq(category)).fetchJoin()
                .where(category.parentCategory.isNull())
                .fetch();
    }
}
